package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.bean;

import java.util.Date;

public class Device {
    private String id;

    private Long tid;

    private Long vid;

    private String no;

    private String authCode;

    private Long sim;

    private Integer typeId;

    private String sofVer;

    private Byte status;

    private Date ct;

    private Long cuid;

    private Date mt;

    private Long muid;

    //终端型号
    private String model;
    //供应商代码
    private String code;
    //供应商名称
    private String chnname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public Long getVid() {
        return vid;
    }

    public void setVid(Long vid) {
        this.vid = vid;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public Long getSim() {
        return sim;
    }

    public void setSim(Long sim) {
        this.sim = sim;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getSofVer() {
        return sofVer;
    }

    public void setSofVer(String sofVer) {
        this.sofVer = sofVer;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCt() {
        return ct;
    }

    public void setCt(Date ct) {
        this.ct = ct;
    }

    public Long getCuid() {
        return cuid;
    }

    public void setCuid(Long cuid) {
        this.cuid = cuid;
    }

    public Date getMt() {
        return mt;
    }

    public void setMt(Date mt) {
        this.mt = mt;
    }

    public Long getMuid() {
        return muid;
    }

    public void setMuid(Long muid) {
        this.muid = muid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChnname() {
        return chnname;
    }

    public void setChnname(String chnname) {
        this.chnname = chnname;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", tid=" + tid +
                ", vid=" + vid +
                ", no='" + no + '\'' +
                ", authCode='" + authCode + '\'' +
                ", sim=" + sim +
                ", typeId=" + typeId +
                ", sofVer='" + sofVer + '\'' +
                ", status=" + status +
                ", ct=" + ct +
                ", cuid=" + cuid +
                ", mt=" + mt +
                ", muid=" + muid +
                ", model='" + model + '\'' +
                ", code='" + code + '\'' +
                ", chnname='" + chnname + '\'' +
                '}';
    }
}
