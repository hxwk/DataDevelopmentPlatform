package com.dfssi.dataplatform.datasync.service.client;

import com.dfssi.dataplatform.datasync.common.platform.entity.TaskAction;
import com.dfssi.dataplatform.datasync.common.platform.entity.TaskEntity;
import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.dfssi.dataplatform.datasync.flume.agent.node.Application;
import com.dfssi.dataplatform.datasync.flume.agent.node.SimpleMaterializedConfiguration;
import com.dfssi.dataplatform.datasync.flume.agent.source.AbstractSource;
import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;
import com.dfssi.dataplatform.datasync.service.master.clientmanagement.TaskStatus;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcRequest;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcResponse;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cxq on 2017/12/27.
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);
    private ConcurrentHashMap<String,Application> appMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,AbstractSource> sourceMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        //这个地方client端开始接收到master端发送的任务
        RpcRequest rpcRequest = new RpcRequest();
        //减少心跳日志输出 平均60s打印一次心跳日志
        int i = (int)(Math.random() * 15);
        if(i == 15) {
            LOGGER.info("client的clientHandler生成心跳消息，当master与client都存活时，必须保持连接且生成心跳消息，才证明程序正常");
        }
        if (response.getMsg() instanceof TaskEntity) { //任务下发
            LOGGER.info("client接收到master下发的任务！根据任务中的clientId和当前的客户端匹配来决定是否执行该任务！");
            Thread.currentThread().setName("taskThread");
            TaskEntity task = (TaskEntity) response.getMsg();
            LOGGER.info("client客户端线程"+Thread.currentThread().getName()+"收到任务:{}", new Gson().toJson(task));
            //LOGGER.info("client客户端收到任务client标志:{}", task.getClientId());
            //LOGGER.info("client客户端服务器上的client标志:{}", PropertiUtil.getStr("client_name"));
            if(!task.getClientId().equals(PropertiUtil.getStr("client_name"))){
                LOGGER.info("任务中的clientId:"+task.getClientId()+"和该clent下的配置文件中的client_name:"+PropertiUtil.getStr("client_name")+"不一致，不执行该任务！");
                return;
            }
            TaskStatus taskStatus = new TaskStatus();
            String taskId = task.getTaskId();
            taskStatus.setTaskId(taskId);
            Application application = appMap.get(taskId);
            if (application == null) {
                application = new Application();
                appMap.put(taskId, application);
            }
            if (task.getAction().equals(TaskAction.START)) {
                LOGGER.info("client开始启动flume任务！taskId:"+taskId);
                SimpleMaterializedConfiguration simpleMaterializedConfiguration = null;
                try {
                    simpleMaterializedConfiguration = new SimpleMaterializedConfiguration(task,sourceMap);
                    LOGGER.info("client开始启动flume任务,物化配置结束");
                    taskStatus.setAvailable(true);
                } catch (Exception e) {
                    LOGGER.error(Thread.currentThread().getName()+"构造flume任务物化配置异常，若class Not Found请检查插件是否加载即client.properties的plugin.lib.path配置项是否正确", e);
                    taskStatus.setAvailable(false);
                    taskStatus.setErrorMsg("构造flume任务物化配置异常");
                }
                application.handleConfigurationEvent(simpleMaterializedConfiguration);
                application.start();
                taskStatus.setSuccess(true);
            } else if (task.getAction().equals(TaskAction.STOP)) {
                LOGGER.info("client开始停止flume任务！taskId:"+taskId);
                //LOGGER.info("application并未停止,测试netty连接是否中断！");
                application.stop();
                appMap.remove(taskId);
                taskStatus.setAvailable(false);
            }
            rpcRequest.setParam("taskStatus", taskStatus);
            channelHandlerContext.channel().writeAndFlush(rpcRequest);
        }else if(response.getMsg() instanceof JtsReqMsg){  //指令下发
            LOGGER.info("client收到master端下发的指令"+response);
            JtsReqMsg msg = (JtsReqMsg) response.getMsg();
            try{
                AbstractSource abstractSource=sourceMap.get("TCPSource");
                if(abstractSource==null){
                    LOGGER.error("sourceMap未实例化，请先发送task任务实例化sourceMap,只有先发送了task任务开启了TCPSource 才能和车辆进行通讯}");
                }else{
                    LOGGER.info("client收到下发指令，开始向车辆终端发送指令：{}",new Gson().toJson(msg));
                    Object result = abstractSource.receive(msg);//改造别人的代码，这里返回已经是空
                    LOGGER.info("client下发指令成功");
                }
            }catch (Exception e){
                LOGGER.info("client下发指令异常",e);
            }
        }else{
            LOGGER.debug("client收到master端未定义类型指令，res:"+response);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
      /*              System.out.println("currentTime:"+currentTime);
                    currentTime++;*/
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setParam("type",Thread.currentThread().getId()+"-heartbeat");
                //HEARTBEAT_SEQUENCE.duplicate()
                ctx.channel().writeAndFlush(rpcRequest);
            }
        }
    }
}
