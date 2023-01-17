//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yaxon.vndp.dms.util;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.yaxon.vndp.dms.*;
import com.yaxon.vndp.dms.exception.DmsException;
import com.yaxon.vndp.dms.util.MsgProps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public abstract class LoadBalancingNodeGroup implements ClusterListener {

    private static final Logger logger = LoggerFactory.getLogger(com.yaxon.vndp.dms.util.LoadBalancingNodeGroup.class);

    private String h;
    protected Set<String> nodeIds = new HashSet();
    protected DmsSystem dmsSystem;

    public LoadBalancingNodeGroup() {
    }

    public void setGroupName(String groupName) {
        this.h = groupName;
    }

    public void setNodeIds(String arr$) {
        String[] var5;
        int len$ = (var5 = StringUtils.split(arr$, ',')).length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String nid = var5[i$];
            this.nodeIds.add(nid.trim());
        }

    }

    public String getRegexNode() {
        return "^" + this.h + ".*";
    }

    public Set<String> getNodeIds() {
        return this.nodeIds;
    }

    public DmsSystem getDmsSystem() {
        return this.dmsSystem;
    }

    public void setDmsSystem(DmsSystem dmsSystem) {
        this.dmsSystem = dmsSystem;
        this.dmsSystem.addClusterListener(this);
        this.updateNodes();
    }

    public void nodesChanged() {
        this.updateNodes();
    }

    protected abstract ServiceNode select(Message var1);

    protected abstract void updateNodes();

    public void send(Message msg) throws DmsException {
        try {
            ServiceNode e;
            logger.warn("msg = " + msg);
            if((e = this.select(msg)) == null) {
                logger.warn("未找到有效的服务节点 " + msg);
                throw new DmsException(3, "未找到有效的服务节点");
            } else {
                MessageProps msgProps;
                logger.warn("MessageProps " + e);
                (msgProps = new MessageProps()).setDest(e.getNodeId());
                logger.warn("MessageProps222 " + e);
                this.dmsSystem.send(msgProps, msg);
            }
        } catch (DmsException var4) {
            throw var4;
        } catch (Exception var5) {
            throw new DmsException(String.format("[%s]发送消息异常:msg=%s", new Object[]{this.h, msg}), var5);
        }
    }

    public void send(MsgProps props, Message msg) throws DmsException {
        try {
            ServiceNode e;
            if((e = this.select(msg)) == null) {
                throw new DmsException(3, "未找到有效的服务节点");
            } else {
                MessageProps msgProps;
                (msgProps = new MessageProps()).setDest(e.getNodeId());
                msgProps.setSeq(props.getSeq());
                msgProps.setMsgType(props.getMsgType());
                msgProps.setReplyTo(props.getReplyTo());
                this.dmsSystem.send(msgProps, msg);
            }
        } catch (DmsException var5) {
            throw var5;
        } catch (Exception var6) {
            throw new DmsException(String.format("[%s]发送消息异常", new Object[]{this.h}), var6);
        }
    }

    public ListenableFuture<Message> call(Message req) {
        try {
            ServiceNode e;
            logger.debug(" call Message " + req);
            if((e = this.select(req)) == null) {
                logger.warn(" 未找到有效的服务节点 " + e);
                throw new DmsException(3, "未找到有效的服务节点");
            } else {
                MessageProps msgProps;
                logger.warn(" 找到有效的服务节点 " + e);
                (msgProps = new MessageProps()).setDest(e.getNodeId());
                return this.dmsSystem.call(msgProps, req);
            }
        } catch (DmsException var4) {
            logger.warn(" DmsException " + var4);
            return Futures.immediateFailedFuture(var4);
        } catch (Exception var5) {
            logger.warn(" Exception " + var5);
            return Futures.immediateFailedFuture(new DmsException(String.format("[%s]发送消息异常", new Object[]{this.h}), var5));
        }
    }

    public ListenableFuture<Message> call(Message req, int timeoutMillis) {
        try {
            ServiceNode e;

            if((e = this.select(req)) == null) {
                throw new DmsException(3, "未找到有效的服务节点");
            } else {
                MessageProps msgProps;
                (msgProps = new MessageProps()).setDest(e.getNodeId());
                return this.dmsSystem.call(msgProps, req, timeoutMillis);
            }
        } catch (DmsException var5) {
            return Futures.immediateFailedFuture(var5);
        } catch (Exception var6) {
            return Futures.immediateFailedFuture(new DmsException(String.format("[%s]发送消息异常", new Object[]{this.h}), var6));
        }
    }
}
