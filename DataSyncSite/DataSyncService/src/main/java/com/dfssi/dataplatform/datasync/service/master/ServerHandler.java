package com.dfssi.dataplatform.datasync.service.master;

import com.dfssi.dataplatform.datasync.common.platform.entity.TaskEntity;
import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.dfssi.dataplatform.datasync.service.master.clientmanagement.TaskStatus;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceDiscovery;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceRegistry;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcRequest;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcResponse;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.ZK_CLIENT_METRIC_DIR;
import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.ZK_TASK_DIR;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    private int loss_connect_time = 0;
    private Map<String, Channel> channelMap = new HashMap<>();
    private ServiceRegistry registor;

    private String isRestartTask = PropertiUtil.getStr("isRestartTask");

    public ServiceRegistry getRegistor() {
        if(registor == null){
            LOGGER.info("registor为空！重新创建一个registor");
            String registryAddress = PropertiUtil.getStr("zk_address");
            registor = new ServiceRegistry(registryAddress);
            return registor;
        }else{
            return registor;
        }
    }

    public void setRegistor(ServiceRegistry registor) {
        this.registor = registor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("server channelRead..");
        LOGGER.debug("server channelRead..");
        LOGGER.debug(ctx.channel().remoteAddress() + "->Server :" + msg.toString());
        super.channelRead(ctx,msg);
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOGGER.debug("ServerHandler.channelActive");
        InetSocketAddress address =   (InetSocketAddress)ctx.channel().remoteAddress();
        //维护channelMap信息
        LOGGER.info("向channelMap中添加key:{}，channelIdValue:{}",address.getAddress().getHostAddress(),ctx.channel().id());
        channelMap.put(address.getAddress().getHostAddress(),ctx.channel());
        //服务端不去帮客户端注册zk信息
        //registor.register(address.getAddress().getHostAddress(), ZK_CLIENT_REGISTRY_PATH);

        if("false".equals(isRestartTask)){
            LOGGER.info("master配置文件中isRestartTask属性为"+isRestartTask+",不重启zk下注册的任务");
        }else{
            LOGGER.info("master配置文件中isRestartTask属性为"+isRestartTask+",重启zk下注册的任务");
            //用于重启的时候检测zookeeper上注册的任务，下发到client，并由client端进行过滤，将属于该客户端的任务重新启动
            restartTask(ctx.channel());
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress address =   (InetSocketAddress)ctx.channel().remoteAddress();
        LOGGER.debug("ServerHandler.channelInactive");
        ctx.channel().close();
        LOGGER.info("向channelMap中删除key:{}，value:{channel}",address.getAddress().getHostAddress());
        channelMap.remove(address.getAddress().getHostAddress());
        //服务端不去帮客户端删除zk信息
        //registor.unRegister(address.getAddress().getHostAddress(), ZK_CLIENT_REGISTRY_PATH);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequst) throws Exception {
        //每隔4s   netty调用该方法
        //LOGGER.info("Master的ServerHandler开始执行channelRead0方法，rpcRequst："+rpcRequst.getRequestId()+",taskStatus:"+rpcRequst.getParam("taskStatus"));
        TaskStatus taskStatus = (TaskStatus)rpcRequst.getParam("taskStatus");
        if(taskStatus==null){
            //减少心跳日志输出 平均20s打印一次心跳日志
            int i = (int)(Math.random() * 5);
            if(i == 4){
                LOGGER.info("Master的ServerHandler生成心跳消息，当master与client都存活时，必须保持连接且生成心跳消息，才证明程序正常");
            }
            RpcResponse response = controllAction(rpcRequst);
            response.setMsg("Hello，我是Server，未发现taskStatus，我的时间戳是"+System.currentTimeMillis());
            //开始向client发送消息
            channelHandlerContext.writeAndFlush(response);
        }else{
            //LOGGER.info("Master的ServerHandler根据前端传来的json串开始生成任务");
            LOGGER.info("master端接收到client端返回信息taskStatus,将执行的任务缓存到zk上");//将执行的任务缓存到zk上
            //InetSocketAddress address =(InetSocketAddress)channelHandlerContext.channel().remoteAddress();
            //获取到client的ip
            //String ipAddr = address.getAddress().getHostAddress();
            //int port = address.getPort();

            LOGGER.info("开始向zk上注册任务的taskStatus临时节点，node:"+taskStatus.getTaskId()+",namespace:"+ZK_TASK_DIR+",data:"+new Gson().toJson(taskStatus));
            ServiceRegistry registorMetric = getRegistor();
            registorMetric.setData(taskStatus.getTaskId(),ZK_CLIENT_METRIC_DIR, new Gson().toJson(taskStatus).getBytes());



            /*registorMetric.register(taskStatus.getTaskId(),ZK_TASK_DIR, JSONUtil.toJson(taskStatus).getBytes());  //这个地方有点问题 待改造
            //在zk上将现在建立连接的client，根据其ip和配置文件上的目录去zk上获取到对应的信息，并转换成指定的class
           ClientMetric metric = registorMetric.getPOJO(ipAddr,ZK_CLIENT_METRIC_DIR,ClientMetric.class);
            if (metric == null) {
                LOGGER.info("获取到metric为空,开始初始化一个metric");
                metric = new ClientMetric();
                metric.setIpAddr(ipAddr);
                metric.setActive(true);
                metric.setRpcPort(String.valueOf(port));
            }else{
                LOGGER.info("获取到metric不为空");
            }
            Map<String, TaskStatus>  map =new HashMap<>();
            map.put(taskStatus.getTaskId(),taskStatus);
            metric.setTaskStatusMap(map);
            LOGGER.info("开始向metric中set数据，data:"+JSONUtil.toJson(metric));
            registorMetric.setData(ipAddr,ZK_CLIENT_METRIC_DIR, JSONUtil.toJson(metric).getBytes());*/
        }
    }

    private RpcResponse controllAction(RpcRequest rpcRequst){
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequst.getRequestId());
        return  response;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                loss_connect_time++;
                LOGGER.debug("5 秒没有接收到客户端的信息了");
                if (loss_connect_time > PropertiUtil.getInt("max_loss_conn_time")) {
                    LOGGER.debug("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public  Map<String, Channel> getChannelMap(){
        LOGGER.info("channelMap:"+channelMap.toString());
        return channelMap;
    }

    public void restartTask(Channel ctx){
        LOGGER.info("客戶端重启的时候检测zookeeper上注册的任务，下发到client，并由client端进行过滤，将属于该Client配置文件注册客户端的任务重新启动");
        try{
            ServiceDiscovery discovery = new ServiceDiscovery( PropertiUtil.getStr("zk_address"),PropertiUtil.getStr("task_dir"),true);
            List<String> allChilrenNode = discovery.getAllChilrenNode();
            for(int i=0;i<allChilrenNode.size();i++){
                ServiceRegistry serviceRegistry = new ServiceRegistry(PropertiUtil.getStr("zk_address"));
                TaskEntity task = serviceRegistry.getPOJO(allChilrenNode.get(i), PropertiUtil.getStr("task_dir"), TaskEntity.class);
                RpcResponse rpcResponse = new RpcResponse();
                rpcResponse.setMsg(task);
                ctx.writeAndFlush(rpcResponse).sync();
                LOGGER.info("zookeeper上注册的任务第"+(i+1)+"个下发成功！");
            }
        }catch (Exception e){
            LOGGER.error("任务重新恢复失败！");
            e.printStackTrace();
        }
    }

}
