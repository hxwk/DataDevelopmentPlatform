package com.dfssi.dataplatform.analysis.entity;

public class DataSourceMeta {
    private long id;
    private String sourceType;
    private String sourceName;
    private String servers;
    private String tableName;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private String createDate;
    private String updateDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public String getCreateDate() {
        if(createDate == null)return null;
        return createDate.endsWith(".0") ? createDate.substring(0, createDate.length() - 2) : createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        if(updateDate == null) return  null;
        return updateDate.endsWith(".0") ? updateDate.substring(0, updateDate.length() - 2) : updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataSourceMeta{");
        sb.append("id=").append(id);
        sb.append(", sourceType='").append(sourceType).append('\'');
        sb.append(", sourceName='").append(sourceName).append('\'');
        sb.append(", servers='").append(servers).append('\'');
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", databaseName='").append(databaseName).append('\'');
        sb.append(", databaseUser='").append(databaseUser).append('\'');
        sb.append(", databasePassword='").append(databasePassword).append('\'');
        sb.append(", createDate='").append(createDate).append('\'');
        sb.append(", updateDate='").append(updateDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}