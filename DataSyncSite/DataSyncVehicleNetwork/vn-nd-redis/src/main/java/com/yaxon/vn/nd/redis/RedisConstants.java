package com.yaxon.vn.nd.redis;

/**
 * Author: 程行荣
 * Time: 2014-06-20 14:26
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */


public class RedisConstants {

    //redis消息推送通道
    public static final String PUSH_MSG_FOR_VEHICLE = "pmsg4v:";
    public static final String PUSH_MSG_FOR_USER = "pmsg4u:";
    public static final String PUSH_MSG_FOR_CHANNEL = "pmsg4c:";

    ///通过spush推送通道往WEB客户端推送的消息
    public static final String PM_T9_9301 = "dxc.9301";
    public static final String PM_T9_9302 = "dxc.9302";
    public static final String PM_T9_9401 = "dxc.9401";
    public static final String PM_T9_9402 = "dxc.9402";
    public static final String PM_T9_9403 = "dxc.9403";
    public static final String PM_T9_9202 = "dxc.9202";
    public static final String PM_T9_9203 = "dxc.9203";
    public static final String PM_T9_9204 = "dxc.9204";
    public static final String PM_T9_9205 = "dxc.9205";
    public static final String PM_T9_9206 = "dxc.9206";


    ///通过spush推送通道往WEB客户端推送的消息
    public static final String PM_T8_0800 = "jts.0800"; //多媒体事件信息上传   //
    public static final String PM_T8_0801 = "jts.0801"; //多媒体数据上传  //
    public static final String PM_T8_0900 = "jts.0900"; //数据上行透传    //
    public static final String PM_GPS_ALARM = "m4v:gps.alarm";     //
    public static final String PM_T8_0301 = "jts.0301"; //事件报告     //不迁移
    public static final String PM_T8_0302 = "jts.0302"; //提问应答    //不迁移
    public static final String PM_T8_0303 = "jts.0303"; //信息点播/取消 //不
    public static final String PM_T8_0701 = "jts.0701"; //电子运单上报   //不
    public static final String PM_T8_0702 = "jts.0702"; //驾驶员身份信息采集上报 //
    public static final String PM_T8_0102 = "jts.0102"; //终端上下线    //
    public static final String PM_CENTER_ALARM = "m4v:center.alarm";
    public static final String PM_OUTOFCITY_ALARM = "m4v:outofcity.alarm";//出城留台报警

    public static final String PM_GATHER_ALARM = "gather.alarm"; //出租车聚集报警
    public static final String PM_SCH_MSG = "pm:sch:msg:"; //文本调度消息
    public static final String PM_EXP_EXCEL = "pm:exp:excel:"; //导出excel消息
    public static final String PM_F001_ND = "pm.f001.nd";//南斗定义的锁车
    public static final String PM_F101_ND = "pm.f101.nd";//南斗定义的锁车操作的结果应答



    ///系统内部模块间交互的消息
    public static final String IM_T8_0801 = "im:t8:0801:"; //多媒体信息上报
    public static final String IM_T8_0200 = "im:t8:0200:"; //位置信息汇报
    public static final String IM_T8_ALARM = "im:t8:alarm:"; //推送报警信息
    public static final String IM_T8_0102 = "im:t8:0102:"; //终端鉴权（车辆上下线）
    public static final String IM_T8_0102_SIM = "im:t8:0102:sim:"; //终端鉴权（车辆上下线）
    public static final String IM_T8_0702 = "im:t8:0702:"; //驾驶员身份信息采集上报
    public static final String IM_T8_0701 = "im:t8:0701:"; //电子运单上报
    public static final String IM_T8_REPLY = "im:t8:reply:"; //用户意见反馈通知
    public static final String IM_T8_APP_REPAYMENT = "im:t8:repayment:"; //还款到期 通知 发给APP
    public static final String IM_T8_APP_SIGNIN = "im:t8:signin:"; //活动签到 通知 发给APP
    public static final String IM_VEHICLE="im:t8:vehicle:";//车辆增加修改 1：增加 2：修改 3：删除
    public static final String IM_RESERVATION_FINISHED = "im:t8:reservation:";//预约服务完成
    public static final String IM_RESCUE_FINISHED = "im:t8:rescue:";//救援服务完成
    public static final String IM_T8_1403 = "im:t8:1403:"; //主动上报报警处理结果

    public static final String IM_T8_IpKeyConfig ="im:t8:IpKeyConfig:";//用于通知有更新或修改系统配置中的ipKeyConfig
    ///全局缓存对象主键
    public static final String GK_VEHICLE_STATE = "gk:vs:"; //车辆状态
    public static final String GK_USER_TO_VEHICLES= "u2vs:"; //用户与车辆对应关系
    public static final String GK_LATEST_POS = "gk:lp:"; //车辆最新的位置
    public static final String VEHICLE_ISUSE = "visuse:";//车辆启用状态
    public static final String VEHICLE_LATEST_INFO = "vehicle_latest_info:"; //车辆最新信息
    public static final String VEHILCE_INFO_UPDATE = "vehicle_info_update:"; //车辆信息变更

    ///通过api jpush推送通道往App客户端推送的消息
    public static final String PM_TO_APP = "msg.to.app";//表示消息是推给app的，下面的是消息的类别
    public static final String PM_ALARM = "vehicle.alarm";//报警信息
    public static final String PM_MASS_MSG = "mass.msg";//群发消息  服务推广
    public static final String PM_SERVER_CHANGE = "server.change";//救援、预约等服务产生变化后，推送给终端的提醒
    public static final String PM_SERVER_CHANGE_RESCUE = "server.change.rescue";//救援服务流程变动消息
    public static final String PM_SERVER_CHANGE_BIGCUSTOMER = "server.change.bigCustomer";//大客户服务的流程变动消息  送修单
    public static final String PM_SERVER_CHANGE_RESERVATION = "server.change.reservation";//预约服务的流程变动消息
    public static final String PM_SERVER_CHANGE_ONLINECONSULTING = "server.change.onlineConsulting";//在线咨询流程变动提醒
    public static final String PM_SERVICE_NOTICE_JOB = "service.notice.job";//定时检查服务后的提醒消息
    public static final String PM_SERVICE_NOTICE_JOB_RESCUE = "service.notice.job.rescue";//救援受理后25分钟还没发车的定时作业提醒
    public static final String PM_SERVICE_NOTICE_JOB_RESERVATION = "service.notice.job.reservation";//距离预约服务时间2小时的定时提醒消息
    //灾害天气提醒服务
    public static final String PM_WEATHER_MSG = "pm:weather:msg";//灾害天气信息
    public static final String PM_WEATHER_DO_GAP = "pm:weather:do:gap";//提醒服务执行间隔时间
    public static final String PM_CONTRACT_ROUTE_DEL = "pm:contract:route:del";//合同路线删除消息
}
