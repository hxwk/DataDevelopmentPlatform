package com.dfssi.dataplatform.ide.access.mvc.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 接入任务
 * Created by yanghs on 2018/9/25.
 */
@JobHandler("access123JobHandler")
@Component
public class AccessJobHandler extends IJobHandler {
    Logger logger = LoggerFactory.getLogger(AccessJobHandler.class);
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("接入任务");
        for(int i=0;i<100;i++){
//            Thread.sleep(5000);
        }
        return new ReturnT(ReturnT.SUCCESS_CODE, "成功呀！");
    }
}
