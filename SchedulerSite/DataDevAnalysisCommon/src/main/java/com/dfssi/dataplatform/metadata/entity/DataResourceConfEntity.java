package com.dfssi.dataplatform.metadata.entity;

import com.dfssi.dataplatform.analysis.entity.AbstractAnalysisEntity;

import java.util.Date;

//@Alias("DataResourceConfEntity")
public class DataResourceConfEntity extends AbstractAnalysisEntity {

//    private String id;
//    private String dataresId;
//    private String dataresName;
//    private String dataresDesc;
//    private int dataresType;
//    private String ipaddr;
//    private long portNum;
//    private int status;
//    private String delimiter;
//    private String zkList;
//    private String tableName;
//    private int isValid;
//    private Date createDate;
//    private String createUser;
//    private Date updateDate;
//    private String updateUser;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getDataresId() {
//        return dataresId;
//    }
//
//    public void setDataresId(String dataresId) {
//        this.dataresId = dataresId;
//    }
//
//    public String getDataresName() {
//        return dataresName;
//    }
//
//    public void setDataresName(String dataresName) {
//        this.dataresName = dataresName;
//    }
//
//    public String getDataresDesc() {
//        return dataresDesc;
//    }
//
//    public void setDataresDesc(String dataresDesc) {
//        this.dataresDesc = dataresDesc;
//    }
//
//    public int getDataresType() {
//        return dataresType;
//    }
//
//    public void setDataresType(int dataresType) {
//        this.dataresType = dataresType;
//    }
//
//    public String getIpaddr() {
//        return ipaddr;
//    }
//
//    public void setIpaddr(String ipaddr) {
//        this.ipaddr = ipaddr;
//    }
//
//    public long getPortNum() {
//        return portNum;
//    }
//
//    public void setPortNum(long portNum) {
//        this.portNum = portNum;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public String getDelimiter() {
//        return delimiter;
//    }
//
//    public void setDelimiter(String delimiter) {
//        this.delimiter = delimiter;
//    }
//
//    public String getZkList() {
//        return zkList;
//    }
//
//    public void setZkList(String zkList) {
//        this.zkList = zkList;
//    }
//
//    public String getTableName() {
//        return tableName;
//    }
//
//    public void setTableName(String tableName) {
//        this.tableName = tableName;
//    }
//
//    public int getIsValid() {
//        return isValid;
//    }
//
//    public void setIsValid(int isValid) {
//        this.isValid = isValid;
//    }
//
//    public Date getCreateDate() {
//        return createDate;
//    }
//
//    public void setCreateDate(Date createDate) {
//        this.createDate = createDate;
//    }
//
//    public String getCreateUser() {
//        return createUser;
//    }
//
//    public void setCreateUser(String createUser) {
//        this.createUser = createUser;
//    }
//
//    public Date getUpdateDate() {
//        return updateDate;
//    }
//
//    public void setUpdateDate(Date updateDate) {
//        this.updateDate = updateDate;
//    }
//
//    public String getUpdateUser() {
//        return updateUser;
//    }
//
//    public void setUpdateUser(String updateUser) {
//        this.updateUser = updateUser;
//    }
//
//    @Override
//    public long nextIndex() {
//        return 0;
//    }

    private String id;
    private String dataresId;
    private String dataresName;
    private String dataresDesc;
    private String dataresType;
    private String ipaddr;
    private long portNum;
    private int status;
    private String delimiter;
    private String zkList;
    private String databaseName;
    private String tableName;
    private int isValid;
    private Date createDate;
    private String createUser;
    private Date updateDate;
    private String updateUser;
    private String sharedStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public long getPortNum() {
        return portNum;
    }

    public void setPortNum(long portNum) {
        this.portNum = portNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getZkList() {
        return zkList;
    }

    public void setZkList(String zkList) {
        this.zkList = zkList;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getSharedStatus() {
        return sharedStatus;
    }

    public void setSharedStatus(String sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

    @Override
    public long nextIndex() {
        return 0;
    }
}
