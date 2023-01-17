package com.dfssi.rpc.netty.transfers;

import com.dfssi.rpc.netty.buffer.ManagedBuffer;
import com.dfssi.rpc.netty.transfers.blocks.BlockDataManager;
import com.dfssi.rpc.netty.transfers.blocks.BlockFetchingListener;
import com.dfssi.rpc.netty.transfers.blocks.BlockId;
import com.dfssi.rpc.netty.transfers.messages.JsonMessageManager;
import com.dfssi.rpc.netty.util.NodeInfo;
import org.apache.log4j.Logger;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/10/31 14:57
 */
public abstract class TransferService {

    protected final Logger logger = Logger.getLogger(TransferService.class);

    protected int port;
    protected String hostname;

    public abstract void init(BlockDataManager blockDataManager, JsonMessageManager jsonMessageManager);
    public abstract void close();

    public abstract void fetchBlocks(NodeInfo nodeInfo,
                                     String[] blockIds,
                                     BlockFetchingListener blockFetchingListener);

    public abstract void uploadBlock(NodeInfo nodeInfo,
                                     BlockId blockId,
                                     ManagedBuffer managedBuffer);

    public abstract void sendJson(NodeInfo nodeInfo, String jsonStr);


}
