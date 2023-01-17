package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/30 17:23
 */
@Configuration
@Getter
@ToString
public class ExportConfig {
    private final Logger logger = LoggerFactory.getLogger(ExportConfig.class);

    @Value("${spring.ftp.export.host}")
    private String address;

    @Value("${spring.ftp.export.port}")
    private int port;

    @Value("${spring.ftp.export.username}")
    private String username;

    @Value("${spring.ftp.export.password}")
    private String password;

    @Value("${spring.ftp.export.parent.dir}")
    private String parentDir;

    @Value("${spring.ftp.export.connect.retry}")
    private int retry;

    @Value("${export.tmp.dir}")
    private String tmpDir;

    public FTPClient getFtpClient(){
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");

        int t = retry;
        while (t > 0) {
            try {
                ftp.connect(address, port);
                ftp.login(username, password);
                int reply = ftp.getReplyCode();
                if (FTPReply.isPositiveCompletion(reply)) {
                    return ftp;
                }
            } catch (IOException e) {
                t = t - 1;
                logger.error(String.format("第%s次连接ftp失败，%s", (retry - t), toString()), e);
                try {
                    Thread.sleep(200 * (retry - t));
                } catch (InterruptedException e1) { }

            }
        }
        return null;
    }

}
