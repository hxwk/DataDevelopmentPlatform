package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;


/**
 *
 * @Author chenf
 * @Description  SD卡文件上传下载
 * @Date  2018/9/13
 * @Version
 **/
public class SDFileEntity {

    private String id;

    private String vid;

    private String sim;

    private String filePath;

    private String fileType;

    private String paramId;

    private String paramValue;

    private int isWaiting=1;//是否等待  0,1

    private long  waitTime; //等待时间毫秒数

    private int isHisotry;//0 不需要历史记录 1 最新记录

    private int isForce =0;//是否强制下发

    private String[] paramIds;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String[] getParamIds() {
        return paramIds;
    }

    public void setParamIds(String[] paramIds) {
        this.paramIds = paramIds;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getIsWaiting() {
        return isWaiting;
    }

    public void setIsWaiting(int isWaiting) {
        this.isWaiting = isWaiting;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public int getIsHisotry() {
        return isHisotry;
    }

    public void setIsHisotry(int isHisotry) {
        this.isHisotry = isHisotry;
    }

    public int getIsForce() {
        return isForce;
    }

    public void setIsForce(int isForce) {
        this.isForce = isForce;
    }
}
