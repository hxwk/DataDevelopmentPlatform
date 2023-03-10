package com.dfssi.rpc.netty.transfers;

import com.dfssi.rpc.netty.RpcContext;
import com.dfssi.rpc.netty.TransportContext;
import com.dfssi.rpc.netty.buffer.ManagedBuffer;
import com.dfssi.rpc.netty.client.RpcResponseCallback;
import com.dfssi.rpc.netty.client.TransportClient;
import com.dfssi.rpc.netty.client.TransportClientFactory;
import com.dfssi.rpc.netty.server.TransportServer;
import com.dfssi.rpc.netty.transfers.blocks.*;
import com.dfssi.rpc.netty.transfers.messages.JsonMessage;
import com.dfssi.rpc.netty.transfers.messages.JsonMessageManager;
import com.dfssi.rpc.netty.transfers.messages.SimpleJsonMessageManager;
import com.dfssi.rpc.netty.util.JavaUtils;
import com.dfssi.rpc.netty.util.NodeInfo;
import com.dfssi.rpc.netty.util.TransportConf;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/10/31 15:55
 */
public class NettyTransferService extends TransferService {

    private String appId;
    private NodeInfo nodeInfo;

    private TransportContext transportContext;
    private TransportConf transportConf;
    private TransportServer transportServer;
    private TransportClientFactory transportClientFactory;

    public NettyTransferService(String appId, NodeInfo nodeInfo){
        this.appId = appId;
        this.nodeInfo = nodeInfo;
        this.hostname = this.nodeInfo.getTargetIP();
        this.port = this.nodeInfo.getTargetPort();
    }

    @Override
    public void init(BlockDataManager blockDataManager, JsonMessageManager jsonMessageManager) {
        RpcContext rpcContext = RpcContext.get();
        this.transportConf = rpcContext.newTransportConf(appId, Maps.newHashMap());
        if(blockDataManager == null)
            blockDataManager = new LocalFileDataManager(transportConf);

        if(jsonMessageManager == null)
            jsonMessageManager = new SimpleJsonMessageManager();

        NettyRpcServer rpcHandler = new NettyRpcServer(appId, blockDataManager, jsonMessageManager);

        this.transportContext = rpcContext.newTransportContext(transportConf, rpcHandler);
        this.transportServer = rpcContext.newTransportServer(hostname, port, transportContext);
        this.transportClientFactory = rpcContext.newTransportClientFactory(appId, transportContext);

        logger.info(String.format("Server created on %s:%s", hostname, port));
    }

    @Override
    public void close() {
        if (transportServer != null) {
            transportServer.close();
        }
        if (transportClientFactory != null) {
            transportClientFactory.close();
        }
    }

    @Override
    public void fetchBlocks(NodeInfo nodeInfo, String[] blockIds, BlockFetchingListener blockFetchingListener) {
        logger.trace(String.format("Fetch blocks from %s:%s (nodeId %s)",
                nodeInfo.getTargetIP(), nodeInfo.getTargetPort(), nodeInfo.getLocalID()));
        try {
            RetryingBlockFetcher.BlockFetchStarter blockFetchStarter = new RetryingBlockFetcher.BlockFetchStarter() {
                @Override
                public void createAndStart(String[] blockIds, BlockFetchingListener listener) throws IOException, InterruptedException {
                    TransportClient client = transportClientFactory.createClient(nodeInfo.getTargetIP(), nodeInfo.getTargetPort());
                    new OneForOneBlockFetcher(client, appId, nodeInfo.getLocalID(), blockIds, listener,
                            transportConf, null).start();
                }
            };

            int maxRetries = transportConf.maxIORetries();
            if (maxRetries > 0) {
                new RetryingBlockFetcher(transportConf, blockFetchStarter, blockIds, blockFetchingListener).start();
            } else {
                blockFetchStarter.createAndStart(blockIds, blockFetchingListener);
            }
        }catch (Exception e){
            logger.error(String.format("Exception while beginning fetchBlocks: %s", Lists.newArrayList(blockIds)), e);
        }
    }

    @Override
    public void uploadBlock(NodeInfo nodeInfo, BlockId blockId, ManagedBuffer managedBuffer) {
        try {
            TransportClient client = transportClientFactory.createClient(nodeInfo.getTargetIP(), nodeInfo.getTargetPort());
            byte[] data = JavaUtils.bufferToArray(managedBuffer.nioByteBuffer());
            client.sendRpc(new UploadBlock(appId, nodeInfo.getLocalID(), blockId.getName(), data).toByteBuffer(),
                    new RpcResponseCallback() {
                        @Override
                        public void onSuccess(ByteBuffer response) {
                            logger.trace(String.format("Successfully uploaded block %s", blockId));
                        }
                        @Override
                        public void onFailure(Throwable e) {
                            logger.error(String.format("Error while uploading block %s", blockId), e);
                        }
                    });
            //client.close();
        } catch (Exception e) {
            logger.error(String.format("Error while uploading block %s", blockId), e);
        }
    }

    @Override
    public void sendJson(NodeInfo nodeInfo, String jsonStr){
        try {
            TransportClient client = transportClientFactory.createClient(nodeInfo.getTargetIP(), nodeInfo.getTargetPort());
            client.sendRpc(new JsonMessage(appId, nodeInfo.getLocalID(), jsonStr).toByteBuffer(),
                    new RpcResponseCallback() {
                        @Override
                        public void onSuccess(ByteBuffer response) {
                            logger.trace(String.format("Successfully send json %s", jsonStr));
                        }
                        @Override
                        public void onFailure(Throwable e) {
                            logger.error(String.format("Error while send json %s", jsonStr), e);
                        }
                    });
            //client.close();
        } catch (Exception e) {
            logger.error(String.format("Error while send json %s", jsonStr), e);
        }

    }

    public void sendJson(NodeInfo nodeInfo, String jsonStr, RpcResponseCallback callback){
        try {
            TransportClient client = transportClientFactory.createClient(nodeInfo.getTargetIP(), nodeInfo.getTargetPort());
            client.sendRpc(new JsonMessage(appId, nodeInfo.getLocalID(), jsonStr).toByteBuffer(), callback);
        } catch (Exception e) {
            logger.error(String.format("Error while send json %s", jsonStr), e);
        }

    }
}
