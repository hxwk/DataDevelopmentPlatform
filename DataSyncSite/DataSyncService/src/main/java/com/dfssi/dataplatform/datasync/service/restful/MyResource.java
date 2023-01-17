package com.dfssi.dataplatform.datasync.service.restful;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.common.platform.entity.TaskAction;
import com.dfssi.dataplatform.datasync.common.platform.entity.TaskEntity;
import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.common.Message;
import com.dfssi.dataplatform.datasync.service.client.RpcClient;
import com.dfssi.dataplatform.datasync.service.master.RpcServer;
import com.dfssi.dataplatform.datasync.service.restful.entity.RestfulResponse;
import com.dfssi.dataplatform.datasync.service.restful.entity.instruction.MessageType;
import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceRegistry;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcResponse;
import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.*;

@Singleton
@Path("service")
public class MyResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
	private RpcServer rpcServer = RpcServer.getRpcService();
	private ServiceRegistry registry = new ServiceRegistry( PropertiUtil.getStr("zk_address"));


	@Path("clientList")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject clientList(
			@FormParam("address")String  address) {
		LOGGER.info("MyResource.clientList param:{}",address);
		RestfulResponse restful = new RestfulResponse();
		JSONObject json = new JSONObject();
		ZooKeeper zk = null;
		List<String> list = null;
		try {
			CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
			CuratorFramework curatorFramework = builder
					.connectString(PropertiUtil.getStr("zk_address"))
					.retryPolicy( new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES))
					.build();
			curatorFramework.start();
			List<String> clientList = curatorFramework.getChildren().forPath("/"+ZK_CLIENT_REGISTRY_PATH);
//			List<String> metricList = new ArrayList<>();
//			for (int i = 0; i < clientList.size(); i++) {
//				byte[] data = curatorFramework.getData().forPath("/" + /*ZK_CLIENT_METRIC_DIR*/"yubin/registryRPC/client" + "/" + clientList.get(i));
//				metricList.add(new String(data));
//			}
//			restful.setData(metricList);
			json.put("list",clientList);

			LOGGER.info("MyResource.clientList response:{}",json);
			return json;
		} catch (IOException | InterruptedException e) {
//				e.printStackTrace();
			LOGGER.error("MyResource.clientList error:{}",e);
		} catch (KeeperException e) {
//				e.printStackTrace();
			LOGGER.error("MyResource.clientList error:{}",e);
		} catch (JSONException e) {
//				e.printStackTrace();
			LOGGER.error("MyResource.clientList error:{}",e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Path("taskList")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject taskList(
			@FormParam("clientIP")String  clientIP) {
		LOGGER.info("MyResource.taskList param:{}",clientIP);

		RestfulResponse restful = new RestfulResponse();
		JSONObject json = new JSONObject();
		ZooKeeper zk = null;
		List<String> list = null;

		try {
			/*CountDownLatch latch = new CountDownLatch(1);
			String zk_address = PropertiUtil.getStr("zk_address");
			zk = new ZooKeeper(zk_address, Constant.ZK_SESSION_TIMEOUT, event -> {
				if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
					latch.countDown();
				}
			});
			latch.await();

			list = zk.getChildren(ZK_TASK_DIR, false);*/


			CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
			CuratorFramework curatorFramework = builder
					.connectString(PropertiUtil.getStr("zk_address"))
					.retryPolicy( new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES))
					.build();
			curatorFramework.start();
			List<String> taskIdList = curatorFramework.getChildren().forPath("/"+ZK_TASK_DIR);
			List<String> taskList = new ArrayList<>();


			for (int i = 0; i < taskIdList.size(); i++) {
				byte[] data =curatorFramework.getData().forPath("/"+ZK_TASK_DIR+"/"+taskIdList.get(i));
				taskList.add(new String(data));
			}






			/*if (StringUtils.isEmpty(clientIP)) {
				List clientIdList = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					String node = list.get(i);
					byte[] data = zk.getData(node, false, null);
					JSONObject object = new JSONObject(new String(data));
					String taskClient = (String) object.get("clientIP");
					if(taskClient.equalsIgnoreCase(clientIP)){
						clientIdList.add(object);
					}
				}
				list = clientIdList;
			}
*/
			restful.setData(taskList);
			json.put("list",list);
//			System.out.println("json = " + json);
			LOGGER.info("MyResource.taskList response:{}",json);
			return json;
		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
			LOGGER.info("MyResource.taskList IOException:{}",e);
		} catch (KeeperException e) {
//			e.printStackTrace();
			LOGGER.info("MyResource.taskList KeeperException:{}",e);
		} catch (JSONException e) {
//			e.printStackTrace();
			LOGGER.info("MyResource.taskList JSONException:{}",e);
		} catch (Exception e) {


		}
		return null;
	}



	@Path("task")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestfulResponse startTask(TaskEntity task) {
		LOGGER.info("开始调用restful接口，向client转发任务startTask，且传过来的任务属性必须符合规范，否则无响应");
		RestfulResponse restful = new RestfulResponse();
		LOGGER.info("MyResource.startTask params:{}",task);
		RpcResponse rpcResponse = new RpcResponse();
		rpcResponse.setMsg(task);
		try {
			int res = rpcServer.send(rpcResponse);
			if (res == -1){
				LOGGER.error("master向客户端发送命令失败,send()返回-1");
				restful.setReturnCode(-1);
				restful.setReturnMsg("master未能向客户端发送命令，请检查master下是否有client与其相连");
				return restful;
			}else if(res == -2){
				LOGGER.error("master下发的task中必须指定clientId");
				restful.setReturnCode(-1);
				restful.setReturnMsg("master未能向客户端发送命令，请检查master下是否有client与其相连");
				return restful;
			}else if(res == -3){
				LOGGER.error("master无法在zk上根据clientId：{}获取到对应的ip信息，请检查对应的zokeeper节点信息");
				restful.setReturnCode(-1);
				restful.setReturnMsg("master未能在ZK上根据task中的clientId找到对应的clent客户端");
				return restful;
			}else if(res == -4){
				LOGGER.error("master无法根据clientIP映射出channel通道,可能原因：1、client死掉但是临时节点未能马上删除造成的 2、client与master未建立连接，请重启client");
				restful.setReturnCode(-1);
				restful.setReturnMsg("master无法根据clientIP映射出和client的channel通道");
				return restful;
			}else if(res == -999){
				LOGGER.error("RpcServer向client下发任务异常");
				restful.setReturnCode(-1);
				restful.setReturnMsg("master无法根据clientIP映射出和client的channel通道");
				return restful;
			}
			if (task.getAction().equals(TaskAction.START)){
				LOGGER.info("master端start任务成功--并在ZK上目录：{}下注册任务节点node:{},value:{}",ZK_TASK_DIR,task.getTaskId(),new Gson().toJson(task));
				registry.registerRealNode(task.getTaskId(),ZK_TASK_DIR,new Gson().toJson(task).getBytes());
			}else{
				LOGGER.info("master端stop任务成功--并在ZK上目录：{}下删除任务节点node:{},value:{}",ZK_TASK_DIR,task.getTaskId(),new Gson().toJson(task));
				registry.unRegister(task.getTaskId(),ZK_TASK_DIR);
			}
			LOGGER.info("master向client发送任务成功！");
			restful.setReturnCode(0);
			restful.setReturnMsg("master向client发送任务成功！");
			return restful;
		} catch (Exception e) {
			LOGGER.error("master向client发送任务成功，但是注册在ZK上的任务出现异常",e);
			restful.setReturnCode(1);
			restful.setReturnMsg("master向client发送任务失败");
			return restful;
		}

	}




	/*@Path("{sub_path:[a-zA-Z0-9]*}")
    @GET
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Object getResourceName(
    		@PathParam("sub_path") String resourceName,
    		@DefaultValue("Just a test!") @QueryParam("desc") String description,
    		@Context Request request,
    		@Context UriInfo uriInfo,
    		@Context HttpHeaders httpHeader) {
        System.out.println(this.hashCode());

//		将HTTP请求打印出来
		System.out.println("****** HTTP request ******");
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(request.getMethod() + " ");
		strBuilder.append(uriInfo.getRequestUri().toString() + " ");
		strBuilder.append("HTTP/1.1[\\r\\n]");
		System.out.println(strBuilder.toString());
		MultivaluedMap<String, String> headers = httpHeader.getRequestHeaders();
		Iterator<String> iterator = headers.keySet().iterator();
		while(iterator.hasNext()){
			String headName = iterator.next();
			System.out.println(headName + ":" + headers.get(headName) + "[\\r\\n]");
		}
		System.out.println("[\\r\\n]");
		String responseStr =resourceName + "[" + description + "]";
        return responseStr;
    }*/

	@Path("command")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Object issuedInstruction(JSONObject jsonObject) {
		LOGGER.info("master接受到下发指令，comand: {}",jsonObject);
		RpcResponse rpcResponse = new RpcResponse();
		JtsResMsg responseMessage= null;
		String id = "";
		try {
			id = (String) jsonObject.get("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("master根据指令id索引到对应的MessageType，id: {}",id+",MessageType中必须映射在master中RoadVehicleModel已存在的bean否则无法索引到消息处理器,如果接下来没有日志则证明master未实例化消息处理器");
		String reqType = id.replace('.', '_');
		try {

			MessageType type = MessageType.valueOf(reqType);
			Class<? extends Message> req = type.getReqClass();
			Message req_f001_nd =new Gson().fromJson(jsonObject.toString(),req);
			rpcResponse.setMsg(req_f001_nd);
			try {
				responseMessage = type.getResClass().newInstance();
			} catch (Exception e) {
				LOGGER.error("根据reqType:"+reqType+"去反射对应的resClass异常，请检查Master中MessageType中定义的映射关系，及RoadVehicleModel.jar包中是否含有该class",e);
			}
		}catch(Exception e) {
			LOGGER.error("根据reqType:"+reqType+"去获取对应的reqClass并转化成json异常，req："+ reqType.toString()+",请检查Master中MessageType中定义的映射关系，及RoadVehicleModel.jar包中是否含有该class",e);

		}



		try {
			LOGGER.info("开始下发指令，指令信息："+rpcResponse.getMsg());
			int res = rpcServer.send(rpcResponse);
			if (res == 0){
				LOGGER.info("master成功向client下发指令,请在client端检查日志");
				responseMessage.setRc(JtsResMsg.RC_OK);
				responseMessage.setErrMsg("master向客户端发送命令成功");
				return responseMessage;
			}else if(res == -1){
				LOGGER.error("master向客户端发送命令失败,send()返回-1");
				responseMessage.setRc(JtsResMsg.RC_FAIL);
				responseMessage.setErrMsg("master未能向客户端发送命令，请检查master下是否有client与其相连");
				return responseMessage;
			}else if(res == -2){
				LOGGER.error("master在geode执行sql异常，请检查geode");
				responseMessage.setRc(JtsResMsg.RC_FAIL);
				responseMessage.setErrMsg("master校验vid是否有效过程中失败，请检查接入平台的geode库");
				return responseMessage;
			}else if (res == -3){
				LOGGER.error("vid在geode中已注册，根据vid获取到geode中时绑定的sim为空");
				responseMessage.setRc(JtsResMsg.RC_FAIL);
				responseMessage.setErrMsg("master校验vid的过程中发现该vid无效，请检查接入平台的geode库中该vid绑定的sim卡数据是否存在");
				return responseMessage;
			}else if (res == -4){
				LOGGER.error("根据vid获取到最后一次终端注册到接入平台时vid绑定的sim，该sim未在redis中注册鉴权");
				responseMessage.setRc(JtsResMsg.RC_FAIL);
				responseMessage.setErrMsg("master校验vid的过程中发现该vid无效，校验过程中在接入平台的geode库中根据该vid获取到其绑定的sim，请检查该sim在redis的注册鉴权情况");
				return responseMessage;
			}else if(res == -5){
				LOGGER.error("根据vid获取到最后一次终端注册到接入平台时vid绑定的sim，该sim对应的终端上一次注册到接入平台绑定的ip，master根据该ip无法获取到对应channel，请检查指令中的车辆对应的客户端是否关联在当前master下，请联系接入平台");
				responseMessage.setRc(JtsResMsg.RC_FAIL);
				responseMessage.setErrMsg("master无法向指令中的vid绑定的client客户端发送命令，请联系接入平台");
				return responseMessage;
			}else if(res == -6){
				LOGGER.error("master端下发指令失败，vid在geode中未注册，请检查geode下的cvVehicleBaseInfo表中该vid是否注册");
				responseMessage.setRc(JtsResMsg.RC_FAIL);
				responseMessage.setErrMsg("master校验vid的过程中发现该vid无效，请检查geode下的cvVehicleBaseInfo表中该vid是否注册");
				return responseMessage;
			}else if(res == -999){
				responseMessage.setRc(JtsResMsg.RC_FAIL);
				responseMessage.setErrMsg("master向客户端发送指令异常，请检查客户端是否和服务端建立连接");
				return responseMessage;
			}
			responseMessage.setRc(JtsResMsg.RC_OK);
		} catch (Exception e) {
			responseMessage.setRc(JtsResMsg.RC_FAIL);
			responseMessage.setErrMsg(e.getMessage());
		}

/*

		if(id.equals("jts.F001.nd")){
			rpcResponse.setMsg(req_f001_nd);
			responseMessage = new Res_0001();
			try {
				rpcServer.send(rpcResponse);
				responseMessage.setRc(RequestMessage.RC_OK);
			} catch (InterruptedException e) {
				responseMessage.setRc(RequestMessage.RC_FAIL);
				responseMessage.setErrMsg(e.getMessage());
//				e.printStackTrace();

			}
		}*/
		if(null ==responseMessage){
			responseMessage = new JtsResMsg(){
				@Override
				public String id() {
					return null;
				}
			};
			responseMessage.setRc(JtsResMsg.RC_NOT_SUPPORT);
		}
		Map result=new HashMap();
		result.put("data","");
		Map status=new HashMap();
		status.put("code",responseMessage.getRc()==0?"200":"999");
		status.put("msg",responseMessage.getRc()==0?"":"失败");
		status.put("details",responseMessage.getErrMsg() == null ? "" :responseMessage.getErrMsg() );
		result.put("status",status);
		return result;
	}


	@Path("stop")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String issuedInstruction() {
		Runtime.getRuntime().exit(1);
		return "success";
	}


}