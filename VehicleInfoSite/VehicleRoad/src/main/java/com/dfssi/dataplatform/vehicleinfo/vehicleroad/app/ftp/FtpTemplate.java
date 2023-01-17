package com.dfssi.dataplatform.vehicleinfo.vehicleroad.app.ftp;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.FtpConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/6 11:52
 */
@Slf4j
@Getter
public class FtpTemplate {

    private FtpClientPool pool;

    public FtpTemplate(FtpConfig properties) throws Exception {
        FtpClientPool pool = new FtpClientPool(properties.getMaximumPoolSize(), new FtpClientFactory(properties));
        this.pool = pool;
    }



}
