package com.dfssi.dataplatform.datasync.service.rpc.cluster;

import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * 集群相关常量
 */
public interface Constant {

        //去除配置文件中无用的属性
        int ZK_SESSION_TIMEOUT = PropertiUtil.getInt("zk_session_timeout");
        int BASE_SLEEP_TIME_MS =PropertiUtil.getInt("base_sleep_time_ms");
        int MAX_RETRIES =PropertiUtil.getInt("max_retries");
        int REST_SERVICE_BASE_PORT =PropertiUtil.getInt("rest_service_base_port");

        String ZK_CLIENT_REGISTRY_PATH = PropertiUtil.getStr("registry_client");

        String ZK_SERVER_REGISTRY_PATH = PropertiUtil.getStr("registry_server");

        ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
                CharsetUtil.UTF_8));

        String ZK_TASK_DIR = PropertiUtil.getStr("task_dir");
        String ZK_CLIENT_METRIC_DIR = PropertiUtil.getStr("zk_client_metric_dir");

        int READER_IDLE_TIME = PropertiUtil.getInt("reader_idle_time");//读操作空闲秒数
        int WRITE_IDLE_TIME = PropertiUtil.getInt("write_idle_time");//写操作空闲秒数
        int READ_WRITE_IDLE_TIME = PropertiUtil.getInt("read_write_idle_time");//读写空闲秒数
        String REST_PACKAGE = PropertiUtil.getStr("rest_package");

}