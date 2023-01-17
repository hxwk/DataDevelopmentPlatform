package com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary;

import com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.bean.DriverIndicator;
import com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.config.Constants;
import com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.config.XdiamondApplication;
import com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.db.DBCommon;
import com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.db.DBPoolDruidCommon;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.xdiamond.client.XDiamondConfig;
import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.*;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by Hannibal on 2018-03-01.
 */
public class BootStrap {

    private static Logger logger = Logger.getLogger(BootStrap.class);

    static SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static XDiamondConfig commonConfig = XdiamondApplication.getInstance().getxDiamondConfig();

    public static void main(String[] args) {

        CommandLine lines = parseArgs(args);

        String day = lines.getOptionValue("day");

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        if (StringUtils.isBlank(day))  {
            day = dayFormat.format(calendar.getTime());
        }

        logger.info("请求是参数： day = " + day );

        String beginDate = day + " 00:00:00";
        String endDate = day + " 23:59:59";

        try {
            fullFormat.parse(beginDate);
        } catch (java.text.ParseException e) {
            logger.error("输入的day格式不对");

            System.exit(0);
        }

        //获取这个时间段内所有参与指标计算的司机
        Set<String> driverSet = searchDrivers(beginDate, endDate);

        if (driverSet.isEmpty()) {
            logger.error("当前时间没有行程");

            System.exit(0);
        }

        //统计每个司机指标
        List<DriverIndicator> driverIndicatorSet = calIndicator(beginDate, endDate, day, driverSet);

        if (driverIndicatorSet.isEmpty()) {
            logger.error("指标计算结果集合为空");

            System.exit(0);
        }

        //将指标统计结果入库
        storeResult(driverIndicatorSet);

    }

    private static Set<String> searchDrivers(String beginDate, String endDate) {

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<String> driverSet = Sets.newHashSet();

        try {
            conn = DBPoolDruidCommon.getInstance().getConnection(Constants.MOTORCADE_DATASOURCE_ID);

            StringBuilder buf = new StringBuilder();
            String table = commonConfig.getProperty("table.trip_indicators");
            buf.append("SELECT DISTINCT driver_id from ");
            buf.append(table);
            buf.append(" WHERE trip_end_time >= ? and trip_end_time <= ?");

            statement = conn.prepareStatement(buf.toString());
            statement.setTimestamp(1, new java.sql.Timestamp(fullFormat.parse(beginDate).getTime()));
            statement.setTimestamp(2, new java.sql.Timestamp(fullFormat.parse(endDate).getTime()));
            logger.debug(" searchDrivers sql = " + statement.toString());
            result = statement.executeQuery();

            if (null != result) {
                while (result.next()) {
                    String[] driverIds = result.getString("driver_id").split(",");
                    for (String driverId : driverIds) {
                        driverSet.add(driverId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(null, e);
        } finally {
            try {
                DBCommon.close(conn, statement, result);
            } catch (SQLException e) {
                logger.error(null, e);
            }
        }

        return driverSet;
    }

    private static List<DriverIndicator> calIndicator(String beginDate, String endDate, String day, Set<String> driverSet) {
        List<DriverIndicator> driverIndicatorSet = Lists.newArrayList();

        String table = commonConfig.getProperty("table.trip_indicators");

        for (String dirverId : driverSet) {
            Connection conn = null;
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                conn = DBPoolDruidCommon.getInstance().getConnection(Constants.MOTORCADE_DATASOURCE_ID);

                StringBuilder buf = new StringBuilder();
                buf.append("SELECT indicator_id, count(indicator_id) as count, sum(indicator_score) as sum from ");
                buf.append(table);
                buf.append(" where trip_end_time >= ? and trip_end_time <= ? and driver_id like ? GROUP BY indicator_id");

                statement = conn.prepareStatement(buf.toString());
                statement.setTimestamp(1, new java.sql.Timestamp(fullFormat.parse(beginDate).getTime()));
                statement.setTimestamp(2, new java.sql.Timestamp(fullFormat.parse(endDate).getTime()));
                statement.setString(3, "%" + dirverId + "%");
                logger.debug(" searchDrivers sql = " + statement.toString());
                result = statement.executeQuery();

                if (null != result) {
                    while (result.next()) {
                        DriverIndicator bean = new DriverIndicator();
                        bean.setDay(day);
                        bean.setDriverId(dirverId);
                        bean.setIndicatorId(result.getInt("indicator_id"));
                        bean.setDailyCount(result.getInt("count"));
                        bean.setDailySum(result.getDouble("sum"));

                        driverIndicatorSet.add(bean);
                    }
                }
            } catch (Exception e) {
                logger.error(null, e);
            } finally {
                try {
                    DBCommon.close(conn, statement, result);
                } catch (SQLException e) {
                    logger.error(null, e);
                }
            }
        }

        return driverIndicatorSet;
    }

    public static void storeResult(List<DriverIndicator> driverIndicatorSet) {

        String table = commonConfig.getProperty("table.dbha_summary_daily_driver_indicator");

        StringBuilder bud = new StringBuilder();
        bud.append("INSERT INTO ");
        bud.append(table);
        bud.append(" (day, driver_id, indicator_id, daily_count, daily_sum) VALUES ");
        for (int i = 0; i < driverIndicatorSet.size(); i++) {
            DriverIndicator driverIndicator = driverIndicatorSet.get(i);
            bud.append(" ( '");
            bud.append(driverIndicator.getDay());
            bud.append("', '");
            bud.append(driverIndicator.getDriverId());
            bud.append("', ");
            bud.append(driverIndicator.getIndicatorId());
            bud.append(", ");
            bud.append(driverIndicator.getDailyCount());
            bud.append(", ");
            bud.append(driverIndicator.getDailySum());
            bud.append(") ");

            if (i < driverIndicatorSet.size() - 1) {
                bud.append(", ");
            }
        }

        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = DBPoolDruidCommon.getInstance().getConnection(Constants.MOTORCADE_DATASOURCE_ID);

            statement = conn.prepareStatement(bud.toString());
            logger.debug(" searchDrivers sql = " + statement.toString());

            String message = statement.executeUpdate() > 0 ? "指标统计结果入库成功" : "指标统计结果入库失败";
            logger.info(message);

        } catch (Exception e) {
            logger.error(null, e);
        } finally {
            try {
                DBCommon.close(conn, statement, null);
            } catch (SQLException e) {
                logger.error(null, e);
            }
        }
    }

    public static CommandLine parseArgs(String[] args) {
        Options options = new Options();
        options.addOption("help", false, "帮助 打印参数详情");
        options.addOption("day", true, " 可选，String 类型,统计日期格式 yyyy-MM-dd，如2018-02-11");


        CommandLineParser parser = new PosixParser();
        CommandLine lines = null;
        try {
            lines = parser.parse(options, args);

            if(lines.hasOption("help") ){
                HelpFormatter formatter = new HelpFormatter();
                formatter.setOptPrefix(HelpFormatter.DEFAULT_LONG_OPT_PREFIX);
                formatter.printHelp("BootStrap", options);
                System.exit(0);
            }
        } catch (ParseException e) {
            logger.error(null, e);
        }

        return lines;
    }


}
