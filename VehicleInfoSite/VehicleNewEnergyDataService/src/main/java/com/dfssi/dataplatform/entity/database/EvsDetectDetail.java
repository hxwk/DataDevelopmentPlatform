package com.dfssi.dataplatform.entity.database;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/26 9:36
 */
public class EvsDetectDetail {

    private String id;
    private String detectName;
    private String detectDesc;

    private int detectAlarmType;
    private int detectAlarmLevel;
    private String detectAlarmContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetectName() {
        return detectName;
    }

    public void setDetectName(String detectName) {
        this.detectName = detectName;
    }

    public String getDetectDesc() {
        return detectDesc;
    }

    public void setDetectDesc(String detectDesc) {
        this.detectDesc = detectDesc;
    }

    public int getDetectAlarmType() {
        return detectAlarmType;
    }

    public void setDetectAlarmType(int detectAlarmType) {
        this.detectAlarmType = detectAlarmType;
    }

    public int getDetectAlarmLevel() {
        return detectAlarmLevel;
    }

    public void setDetectAlarmLevel(int detectAlarmLevel) {
        this.detectAlarmLevel = detectAlarmLevel;
    }

    public String getDetectAlarmContent() {
        return detectAlarmContent;
    }

    public void setDetectAlarmContent(String detectAlarmContent) {
        this.detectAlarmContent = detectAlarmContent;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EvsDetectDetail{");
        sb.append("id='").append(id).append('\'');
        sb.append(", detectName='").append(detectName).append('\'');
        sb.append(", detectDesc='").append(detectDesc).append('\'');
        sb.append(", detectAlarmType=").append(detectAlarmType);
        sb.append(", detectAlarmLevel=").append(detectAlarmLevel);
        sb.append(", detectAlarmContent='").append(detectAlarmContent).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
