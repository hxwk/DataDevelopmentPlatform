package com.dfssi.dataplatform.quartz.sync.evs;

import org.apache.commons.cli.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/18 20:01
 */
public class EvsCountDataSynFromRedisJob implements Job {
    private final static Logger logger = LoggerFactory.getLogger(EvsCountDataSynFromRedisJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String configDir = jobDataMap.getString("configDir");
        String nameNodeHost = jobDataMap.getString("nameNodeHost");
        int nameNodeWebhdfsPort = jobDataMap.getInt("nameNodeWebhdfsPort");
        try {
            EvsConfig config = new EvsConfig(nameNodeHost, nameNodeWebhdfsPort, configDir);
            EvsCountDataSynFromRedis evsCountDataSynFromRedis =
                    new EvsCountDataSynFromRedis(config);
            evsCountDataSynFromRedis.executeSync();
        } catch (SQLException e) {
            logger.error("同步失败", e);
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {

        CommandLine line = parseArgs(args);
        String configDir = line.getOptionValue("configDir");
        int interval = Integer.parseInt(
                line.getOptionValue("interval", "10"));


        String nameNodeHost = line.getOptionValue("nameNodeHost");
        int nameNodeWebhdfsPort = Integer.parseInt(
                line.getOptionValue("nameNodeWebhdfsPort", "50070"));

        logger.info("新能源数据redis-gp同步参数配置如下：\n");
        logger.info(String.format("configDir ： %s \n  interval  ： %s", configDir, interval));

        //创建scheduler
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        //定义一个Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("EvsCountDataSynFromRedisJob", "group1")
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                //每隔十分钟执行一次
                                .withIntervalInMinutes(interval)
                                //一直执行，奔腾到老不停歇
                                .repeatForever()
                )
                .build();

        //定义一个JobDetail
        //定义Job类为HelloQuartz类，这是真正的执行逻辑所在
        JobDetail job = JobBuilder.newJob(EvsCountDataSynFromRedisJob.class)
                .withIdentity("EvsCountDataSynFromRedisJob", "group1")
                .usingJobData("configDir", configDir)
                .usingJobData("nameNodeHost", nameNodeHost)
                .usingJobData("nameNodeWebhdfsPort", nameNodeWebhdfsPort)
                .build();

        //加入这个调度
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
    }

    private static CommandLine  parseArgs(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("help", false, "帮助 打印参数详情");
        options.addOption("configDir", true, "数据同步的配置目录");
        options.addOption("interval", true, "同步时间间隔，单位：分钟，默认: 10");
        options.addOption("nameNodeHost", true, "cdh集群中hdfs的处于active状态下的namenode的ip或主机名");
        options.addOption("nameNodeWebhdfsPort", true, "webhdfs的端口， 默认：50070");


        CommandLineParser parser = new DefaultParser();
        CommandLine lines = parser.parse(options, args);
        if (lines.hasOption("help")
                || lines.getOptionValue("nameNodeHost") == null)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("EvsCountDataSynFromRedisJob", options);
            System.exit(0);
        }
        return lines;
    }
}
