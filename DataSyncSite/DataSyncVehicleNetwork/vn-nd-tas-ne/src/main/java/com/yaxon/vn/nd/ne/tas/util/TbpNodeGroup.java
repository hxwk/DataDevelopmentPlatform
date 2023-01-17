package com.yaxon.vn.nd.ne.tas.util;

/**
 * Author: 程行荣
 * Time: 2015-01-29 19:29
 * Copyright (C) 2015 Xiamen Yaxon Networks CO.,LTD.
 */

import com.yaxon.vn.nd.tbp.si.JtsReqMsg;
import com.yaxon.vn.nd.tbp.si.JtsResMsg;
import com.yaxon.vndp.dms.Message;
import com.yaxon.vndp.dms.util.ConsistentHashNodeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 终端业务处理模块（tbp）的负载均衡器
 */
public class TbpNodeGroup extends ConsistentHashNodeGroup {
    private static final Logger logger = LoggerFactory.getLogger(TbpNodeGroup.class);

    @Override
    protected long hashForMsg(Message msg) {
        String vin = "";
        if (msg instanceof JtsReqMsg) {
            vin = ((JtsReqMsg)msg).getVin();
        } else if (msg instanceof JtsResMsg) {
            vin = ((JtsResMsg)msg).getVin();
        }

        return hash(vin);
    }
}
