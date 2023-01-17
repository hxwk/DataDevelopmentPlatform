package com.dfssi.dataplatform.manager.monitor.task.service;

import java.text.ParseException;
import java.util.*;

import com.dfssi.dataplatform.manager.monitor.OozieConfig;
import com.dfssi.dataplatform.manager.monitor.task.dao.MonitorTaskExecRecordDao;
import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskEntity;
import com.dfssi.dataplatform.manager.monitor.task.entity.MonitorTaskExecRecordEntity;
import com.dfssi.dataplatform.manager.monitor.task.utils.JacksonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OozieTaskStatusRefresh extends AbstractService {

    private static final String LOG_PREFIX_REFRESH_OOZIE = "[Refresh Oozie]";

    public static final String OOZIE_JOB_STATUS_SUCCEEDED = "SUCCEEDED";
    public static final String OOZIE_JOB_STATUS_KILLED = "KILLED";
    public static final String OOZIE_JOB_STATUS_SUSPENDED = "SUSPENDED";
    public static final String OOZIE_TIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    private static volatile boolean executing = false;
    private static HttpClient httpClient = null;
    private static Integer lock = 0;

    @Autowired
    private OozieConfig oozieConfig;
    @Autowired
    private MonitorTaskExecRecordDao taskExecRecDao;

    private HttpClient getHttpClient() {
        if (httpClient != null) {
            return httpClient;
        } else {
            synchronized (lock) {
                if (httpClient == null) httpClient = HttpClientBuilder.create().build();

                return httpClient;
            }
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refresh() {
        if (executing) return;

        executing = true;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("taskType", MonitorTaskEntity.TASK_TYPE_OOZIE);

            while (true) {
                List<MonitorTaskExecRecordEntity> histories = this.taskExecRecDao.listOozieTasks(params);
                if (histories == null || histories.size() <= 0) break;
                for (MonitorTaskExecRecordEntity eh : histories) {
                    this.refreshStatus(eh);
                }
            }
            logger.info(LOG_PREFIX_REFRESH_OOZIE + "Refresh job status once.");
        } catch (Exception e) {
            logger.error(LOG_PREFIX_REFRESH_OOZIE + "Refresh job error.", e);
        } finally {
            executing = false;
        }
    }

    public String getOozieJobInfo(String jobId) throws Exception {
        HttpGet httpGet = new HttpGet(oozieConfig.getOozieRestApiUrl() + jobId + "?show=info&timezone=" + TimeZone
                .getDefault().getID());

        HttpResponse response = getHttpClient().execute(httpGet);
        String resStr = EntityUtils.toString(response.getEntity());

        return resStr;
    }

    private void refreshStatus(MonitorTaskExecRecordEntity mtere) {
        try {
            if (StringUtils.isBlank(mtere.getExecAppId())) return;

            String info = getOozieJobInfo(mtere.getExecAppId());
            if (StringUtils.isBlank(info)) return;

            JsonObject infoJson = new JsonParser().parse(info).getAsJsonObject();

            String endTimeStr = JacksonUtils.getAsString(infoJson, "endTime");
            mtere.setEndTime(parseDate(endTimeStr));
            mtere.setStatus(JacksonUtils.getAsString(infoJson, "status"));

            this.taskExecRecDao.updateTaskStatus(mtere);
        } catch (Throwable t) {
            logger.error(LOG_PREFIX_REFRESH_OOZIE + "Refresh job error.", t);
        }
    }

    private Date parseDate(String timeStr) throws ParseException {
        try {
            if (StringUtils.isBlank(timeStr)) return null;

            return DateUtils.parseDate(timeStr, Locale.ENGLISH, OOZIE_TIME_PATTERN);
        } catch (Exception e) {
            return DateUtils.parseDate(timeStr, Locale.SIMPLIFIED_CHINESE, OOZIE_TIME_PATTERN);
        }
    }

}
