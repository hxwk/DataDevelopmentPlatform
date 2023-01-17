package com.dfssi.dataplatform.datasync.service.master;

import com.dfssi.dataplatform.datasync.common.platform.entity.TaskEntity;
import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceDiscovery;
import com.dfssi.dataplatform.datasync.service.util.geodeUtil.Constants;
import com.dfssi.dataplatform.datasync.service.util.geodeUtil.GeodeTool;
import com.dfssi.dataplatform.datasync.service.util.geodeUtil.RedisPoolManager;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcDecoder;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcEncoder;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcRequest;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcResponse;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.CVVehicleDTO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.SelectResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.*;
import static java.lang.Thread.sleep;


public class RpcServer  {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private static RpcServer server;
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    //做开启和关闭 需要保存关闭对象的引用
    EventLoopGroup bossGroup = null;
    EventLoopGroup workerGroup = null;
    Channel channel = null;
    ServerHandler serverHandler = null;
    ServerBootstrap  bootstrap = null;
    boolean connected = false;

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private RpcServer(String serverAddress, ServiceDiscovery serviceDiscovery) {
        this.serverAddress = serverAddress;
        this.serviceDiscovery = serviceDiscovery;
    }

    static {
        LOGGER.info("开始启动master的netty客户端");
        String registryAddress = PropertiUtil.getStr("zk_address");
        String serverAddress = PropertiUtil.getStr("server_address");
        String zkClientRegistryPath = PropertiUtil.getStr("registry_client");
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery(registryAddress, zkClientRegistryPath,true);
        server = new RpcServer(serverAddress,serviceDiscovery);
        try {
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread("RpcServer"){
                @Override
                public void run() {
                    try {
                        server.stop();
                    } catch (Exception e) {
                        LOGGER.error("stop RpcServer error! message:{}",e);
                    }
                }
            });
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.error("stop RpcServer error! message:{}",e);
        }
    }



    public static  RpcServer getRpcService(){
        return server;
    }


    /*public static void main(String[] args) {
        RpcServer test = new RpcServer();
        test.send();
    }*/


    public int send(RpcResponse response) throws  InterruptedException{
        //服务端发送Task给客户端
        LOGGER.info("RpcServer开始发送命令，request = [" + response + "]");
        Map<String, Channel> map = serverHandler.getChannelMap();
        if(map.size() == 0){
            LOGGER.error("master端未发现相连的client端，无法下发指令，请检查client是否存活！serverHandler的channel信息map:"+map.toString());
            return -1;
        }
        if(response.getMsg() instanceof TaskEntity){
            TaskEntity task = (TaskEntity)response.getMsg();
            String clientId = task.getClientId();
            LOGGER.info("获取到TaskEntity的ClientId为{},并根据该clientId路由到对应的客户端执行task:{}",clientId,task);
            String clientIP = "";
            if (clientId == null ||"".equals(clientId) ){
                LOGGER.error("master下发的task中必须指定clientId");
                return -2;
            }else{
                clientIP = serviceDiscovery.discover(clientId);
            }

            if("".equals(clientIP)){
                LOGGER.error("RpcServer无法在zk上根据clientId：{}获取到对应的ip信息，请检查对应的zokeeper节点信息",clientId);
                return -3;
            }
            LOGGER.info("根据clientId:{}路由到对应的客户端ip:{}",clientId,clientIP);
            Channel channel = map.get(clientIP);
            //如果channel为null 那么则无法从clientIP映射出channel
            if (channel == null){
                LOGGER.info("master端的serverHandler的channel信息map:"+map.toString());
                //当客户端死掉后，临时节点不能马上删除，所以还可以取到ip,但是channel已经关闭
                LOGGER.error("RpcServer无法根据clientIP：{}映射出channel通道,可能原因：1、client死掉但是临时节点未能马上删除造成的 2、client与master未建立连接，请重启client",clientIP);
                return -4;
            }
            LOGGER.info("master端校验client连接通过，开始发送开启flume任务的指令到client端");
            try {
                //这个地方开始发送任务给client端
                channel.writeAndFlush(response).sync();
            } catch (InterruptedException e) {
                LOGGER.error("RpcServer向client下发任务异常{}",e);
                return -999;
            }
            LOGGER.info("master端发送开启flume任务的指令到client端成功！");
            return 0;
        }
        //服务端发送指令给客户端
        if(response.getMsg() instanceof JtsReqMsg){
            LOGGER.info("master端开始下发指令到client端!");
            try {
                String JtsReqMsgVid = ((JtsReqMsg) response.getMsg()).getVid();
                //本地调试模式，如果部署在服务器上，应该将many_client属性不置为many_client
                String many_client = PropertiUtil.getStr("many_client");
                if ("local".equals(many_client)) {
                    LOGGER.info("master端执行策略为本地调试策略，开始下发任务指令到client端！");
                    Collection<Channel> channels = map.values();
                    for (Iterator<Channel> iterator = channels.iterator(); iterator.hasNext(); ) {
                        Channel channel = iterator.next();
                        channel.writeAndFlush(response).sync();
                        LOGGER.info("master端开始下发任务指令到client端成功！");
                    }
                    return 0;
                }else{
                    //先考虑指令中的vid是否在geode注册，不管该vid对应的车是否有效
                    LOGGER.info("master端开始下发任务指令执行策略，查询指令中的vid是否在geode注册，根据vid获取到最后一次终端注册到接入平台时vid绑定的sim，" +
                            "再根据sim去redis中校验该终端是否登录鉴权，最后根据sim绑定的外网ip下发到对应的client！");
                    String sql="select * from  /cvVehicleBaseInfo where vid='"+JtsReqMsgVid+"'";
                    LOGGER.info("在geode执行sql,查询此vid是否在geode注册:"+sql);
                    Region regeion = GeodeTool.getRegeion(Constants.REGION_CVVEHICLEBASEINFO);
                    SelectResults vidRes=null;
                    try{
                        vidRes = regeion.query(sql);
                    }catch (Exception e){
                        e.printStackTrace();
                        LOGGER.error("在geode执行sql异常，请检查geode,{}"+sql,e);
                        return -2;
                    }
                    LOGGER.info("请检查VehicleInfoModel.jar是否存在，若不存在则无法下发指令");
                    if(vidRes.size()>0){
                        LOGGER.info("vid在geode中已注册，根据vid获取到最后一次终端注册到接入平台时vid绑定的sim，再根据sim去redis中校验该终端是否登录鉴权");
                        CVVehicleDTO vidWithGeode=  (CVVehicleDTO)vidRes.asList().get(0);
                        String vidReferenceSim = vidWithGeode.getSim();
                        if("".equals(vidReferenceSim) || null == vidReferenceSim){
                            LOGGER.error("vid在geode中已注册，根据vid获取到geode中时绑定的sim为空,vidReferenceSim:"+vidReferenceSim);
                            return -3;
                        }
                        Jedis jedis = new RedisPoolManager().getJedis();
                        String simReferenceIp = jedis.get("SimBinding:"+vidReferenceSim);
                        jedis.close();
                        if(simReferenceIp==null){
                            LOGGER.error("根据vid获取到最后一次终端注册到接入平台时vid绑定的sim:"+vidReferenceSim+"，该sim未在redis中注册鉴权,simReferenceIp:"+simReferenceIp);
                            return -4;
                        }
                        Channel channel = map.get(simReferenceIp);
                        if(channel==null){
                            //这种情况属于bug，按代码逻辑，client死掉后，其对应的sim卡也会从redis中移除，redis中只要注册的有sim，就代表该对应的终端已连上接入平台
                            LOGGER.error("根据vid获取到最后一次终端注册到接入平台时vid绑定的sim:"+vidReferenceSim+"，该sim对应的终端上一次注册到接入平台绑定的ip:"+simReferenceIp+"下不存在client客户端，请联系接入平台,map:"+map.toString());
                            return -5;
                        }
                        LOGGER.info("master端校验指令中的vid通过，开始向对应的终端发送指令");
                        channel.writeAndFlush(response).sync();
                        LOGGER.info("master端开始下发任务指令到client端成功！");
                    }else{
                        LOGGER.error("master端下发失败，vid在geode中未注册，请检查geode下的cvVehicleBaseInfo表中该vid是否注册，vid："+((JtsReqMsg) response.getMsg()).getVid());
                        return -6;
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("RpcServer向client下发指令异常{}",e);
                return -999;
            }
            return 0;
        }
        return 0;
    }




    /**
     * 启动服务
     */
    public void start()  {
        connected = false;
        LOGGER.info("RpcServer-master客户端开始启动服务");
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        serverHandler = new ServerHandler();
        //serverHandler.setRegistor(serviceRegistry);
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(READER_IDLE_TIME, WRITE_IDLE_TIME, READ_WRITE_IDLE_TIME, TimeUnit.SECONDS))
                                .addLast(new RpcDecoder(RpcRequest.class)) // 将 RPC 请求进行解码（为了处理请求）
                                .addLast(new RpcEncoder(RpcResponse.class)) // 将 RPC 响应进行编码（为了返回响应）
                                .addLast(serverHandler); // 处理 RPC 请求
                    }
                })
                /**
                 * 对于ChannelOption.SO_BACKLOG的解释：
                 * 服务器端TCP内核维护有两个队列，我们称之为A、B队列。客户端向服务器端connect时，会发送带有SYN标志的包（第一次握手），服务器端
                 * 接收到客户端发送的SYN时，向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，然后服务器接收到
                 * 客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移动到B队列，连接完成，应用程序的accept会返回。也就是说accept
                 * 从B队列中取出完成了三次握手的连接。
                 * A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，新的连接将会被TCP内核拒绝。
                 * 所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户端无法连接。要注意的是，backlog对程序支持的
                 * 连接数并无影响，backlog影响的只是还没有被accept取出的连接
                 */
                .option(ChannelOption.SO_BACKLOG, PropertiUtil.getInt("netty.so_backlog"))
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        doConnect();
    }

    ////连接
    public void doConnect(){
        if (connected)
            return;
        LOGGER.info("RpcServer-master客户端开始doConnect");//master端可以绑定自己的ip，也可以不绑定自己的ip
        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        try {
            ChannelFuture future = bootstrap.bind(host, port).sync();
            channel = future.channel();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        LOGGER.info("RpcServer-master客户端doConnect成功,ip:"+host+"，端口:"+port);
                        connected = true;
                    } else {
                        LOGGER.error("RpcServer-master客户端doConnect失败");
                        // future.channel().eventLoop().schedule(() -> doConnect(), 5, TimeUnit.SECONDS);
                        final EventLoop loop = future.channel().eventLoop();
                        loop.schedule(new Runnable() {
                            @Override
                            public void run() {
                                doReconnect();
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("RpcServer-master客户端doConnect失败,请检查master客户端的ip:"+host+"是否正确，端口:"+port+"是否被占用" ,e);
            // doReconnect();
        }
    }


    /*  重连 */
    public void doReconnect(){
        LOGGER.info("RpcServer-master客户端doReconnect");
        stop();
        try {
            sleep(2 * 1000L);
        } catch (InterruptedException e1) {
            LOGGER.error("RpcServer-master客户端sleep异常，请检查系统环境 error :{}",e1);
        }
        start();
    }

    //停止服务，关闭连接 并且注销服务地址
    public void stop()  {
        LOGGER.info("RpcServer-master客户端开始停止服务关闭连接");
        try {
            if (channel!=null){
                channel.close();
            }
        }catch (Throwable e){
//            e.printStackTrace();
            LOGGER.error("RpcServer-master客户端停止服务关闭连接失败 error:{}",e);
        }

        Collection<Channel> channels = serverHandler.getChannelMap().values();
        try {
            channels.forEach((channel1 -> {
                if (channel1.isActive()){
                    channel1.close();
                }
            }));
        } catch (Exception e) {
            LOGGER.error("RpcServer-master客户端停止服务关闭连接失败 error:{}",e);

        }

        if (bossGroup != null) {
            try {
                bossGroup.shutdownGracefully();
            } catch (Exception e) {
                LOGGER.error("RpcServer-master客户端停止服务关闭连接失败 error:{}",e);

            }
        }

        if (workerGroup != null) {
            try {
                workerGroup.shutdownGracefully();
            } catch (Exception e) {
                LOGGER.error("RpcServer-master客户端停止服务关闭连接失败 error:{}",e);
            }
        }
        //因为是临时节点 所以不再需要删除server端节点的逻辑
        /*try {
            String serverIP = InetAddress.getLocalHost().getHostAddress();
            serviceRegistry.unRegister(serverIP,Constant.ZK_SERVER_REGISTRY_PATH);
        }catch (Exception e){
            LOGGER.error("RpcServer->stop unRegister error ,e = {}",e);
        }*/
        connected = false;
    }
}