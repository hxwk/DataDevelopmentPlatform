package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.analyze;

import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.exception.UnsupportedProtocolException;

/**
 * Created by Hannibal on 2018-04-09.
 */
public class TerminalHandler extends BaseProtoHandler {

    private static final byte platformLoginCode = 0x05;
    private static final byte platformLogoutCode = 0x06;

    private static final byte vehicleLoginCode = 0x01;
    private static final byte vehicleLogoutCode = 0x04;

    @Override
    public String doUpMsg(ProtoMsg upMsg) {
        if (vehicleLoginCode == upMsg.commandSign) {
            return do_01(upMsg);
        } else if (vehicleLogoutCode == upMsg.commandSign) {
            return do_04(upMsg);
        } else if (platformLoginCode == upMsg.commandSign) {
            return do_05(upMsg);
        } else if (platformLogoutCode == upMsg.commandSign) {
            return do_06(upMsg);
        } else {
            throw new UnsupportedProtocolException("未知的上行请求消息：msgId=" + upMsg.commandSign);
        }
    }

    /**
     * 车辆登入
     * @param upMsg
     * @return
     */
    private String do_01(final ProtoMsg upMsg) {
        return new String(upMsg.dataBuf.array());
    }

    /**
     * 车辆登出
     * @param upMsg
     * @return
     */
    private String do_04(final ProtoMsg upMsg) {
        return new String(upMsg.dataBuf.array());
    }

    /**
     * 平台登入
     * @param upMsg
     * @return
     */
    private String do_05(final ProtoMsg upMsg) {
        return new String(upMsg.dataBuf.array());
    }

    /**
     * 平台登出
     * @param upMsg
     * @return
     */
    private String do_06(final ProtoMsg upMsg) {
        return new String(upMsg.dataBuf.array());
    }
}
