package com.dfssi.rpc.netty.transfers.blocks;

import com.dfssi.rpc.netty.buffer.ManagedBuffer;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/10/31 14:54
 */
public interface BlockDataManager {

    ManagedBuffer getBlockData(BlockId blockId);

    boolean putBlockData(BlockId blockId, ManagedBuffer data);

    void releaseLock(BlockId blockId, long taskAttemptId);
}
