package com.dfssi.dataplatform.devmanage.dataresource.mvc.entity;

import com.dfssi.dataplatform.devmanage.dataresource.mvc.base.BaseVO;
import org.apache.ibatis.type.Alias;

import java.sql.Date;
import java.util.List;

@Alias("dataResourceEntity")
public class DataResourceEntity  extends BaseVO {

    private String dataresId;
    private String dataresName;
    private String dataresDesc;
    private String dataresType;
    private Integer status;
    private Integer sharedStatus;

    private List<DataResourceAccessEntity> accessEntities;

    public String getDataresId() {
        return dataresId;
    }

    public void setDataresId(String dataresId) {
        this.dataresId = dataresId;
    }

    public String getDataresName() {
        return dataresName;
    }

    public void setDataresName(String dataresName) {
        this.dataresName = dataresName;
    }

    public String getDataresDesc() {
        return dataresDesc;
    }

    public void setDataresDesc(String dataresDesc) {
        this.dataresDesc = dataresDesc;
    }

    public String getDataresType() {
        return dataresType;
    }

    public void setDataresType(String dataresType) {
        this.dataresType = dataresType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSharedStatus() {
        return sharedStatus;
    }

    public void setSharedStatus(Integer sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

    public List<DataResourceAccessEntity> getAccessEntities() {
        return accessEntities;
    }

    public void setAccessEntities(List<DataResourceAccessEntity> accessEntities) {
        this.accessEntities = accessEntities;
    }

    @Override
    public String toString() {
        return "DataResourceEntity{" +
                "dataresId='" + dataresId + '\'' +
                ", dataresName='" + dataresName + '\'' +
                ", dataresDesc='" + dataresDesc + '\'' +
                ", dataresType='" + dataresType + '\'' +
                ", status=" + status +
                ", sharedStatus=" + sharedStatus +
                ", createUesr='" + getCreateUser() + '\'' +
                ", createDate='" + getCreateDate() + '\'' +
                ", updateUesr='" + getUpdateUser() + '\'' +
                ", updateUesr='" + getUpdateDate() + '\'' +
                '}';
    }
}
