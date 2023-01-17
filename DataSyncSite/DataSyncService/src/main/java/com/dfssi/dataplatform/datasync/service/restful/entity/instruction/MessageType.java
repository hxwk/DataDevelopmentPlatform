package com.dfssi.dataplatform.datasync.service.restful.entity.instruction;


import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;
import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;
import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.*;

/**
 * Created by HSF on 2018/1/18.
 */
public enum  MessageType {

    jts_F001_nd("jts.F001.nd",Req_F001_nd.class,Res_0001.class),
    jts_F003_nd("jts.F003.nd",Req_F003_nd.class,Res_0001.class),
    jts_F006_nd("jts.F006.nd",Req_F006_nd.class,Res_0001.class),
    jts_F007_nd("jts.F007.nd",Req_F007_nd.class,Res_0001.class),
    jts_F008_nd("jts.F008.nd",Req_F008_nd.class,Res_0001.class),
    jts_9101_nd("jts.9101.nd",Req_9101_nd.class,Res_0001.class),
    jts_9102_nd("jts.9102.nd",Req_9102_nd.class,Res_0001.class),
    jts_9003("jts.9003",Req_9003.class,Res_0001.class),
    jts_8107("jts.8107",Req_8107.class,Res_0001.class),
    jts_8108("jts.8108",Req_8108.class,Res_0001.class),
    jts_9201("jts.9201",Req_9201.class,Res_0001.class),
    jts_9202("jts.9202",Req_9202.class,Res_0001.class),
    jts_9205("jts.9205",Req_9205.class,Res_0001.class),
    jts_8300("jts.8300",Req_8300.class,Res_0001.class),
    jts_8801("jts.8801",Req_8801.class,Res_0001.class),
    jts_8802("jts.8802",Req_8802.class,Res_0802.class),
    jts_8803("jts.8803",Req_8803.class,Res_0001.class),
    jts_8804("jts.8804",Req_8804.class,Res_0001.class),
    jts_8805("jts.8805",Req_8805.class,Res_0001.class),

    jts_8105("jts.8105",Req_8105.class,Res_0001.class),

    //道路试验车--设置终端参数8103
    jts_8103("jts.8103",Req_8103.class,Res_0001.class),
    //道路试验车--查询终端参数8104
    jts_8104("jts.8104",Req_8104.class,Res_0001.class),
    //道路试验车--查询指定的终端参数8106
    jts_8106("jts.8106",Req_8106.class,Res_0001.class),

    jts_E101("jts.E101",Req_E101.class,Res_E001.class),
    jts_E102("jts.E102",Req_E102.class,Res_0001.class),
    jts_E103("jts.E103",Req_E103.class,Res_0001.class),
    OTHER(null,null,null);


    private String id;
    private  Class<? extends JtsReqMsg> reqClass;
    private Class<? extends JtsResMsg> resClass;

    MessageType(String id,Class<? extends JtsReqMsg> reqClass,Class<? extends JtsResMsg> resClass) {
        this.id = id;
        this.reqClass = reqClass;
        this.resClass = resClass;
    }

    public Class<? extends JtsReqMsg> getReqClass() {
        return reqClass;
    }

    public Class<? extends JtsResMsg> getResClass() {
        return resClass;
    }

}
