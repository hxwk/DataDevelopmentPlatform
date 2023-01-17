package com.yaxon.vn.nd.tbp.si;

/**
 * @author jianKang
 * @date 2018/01/26
 */
public class CanVid2FileIdBean {
    private String vid;//车辆id
    private String vehichleType;//车辆类型
    private String dbcFileName;//DBC 文件名
    private String dbcFastDFSFileId;//DBC在FastDFS的FileId

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getVehichleType() {
        return vehichleType;
    }

    public void setVehichleType(String vehichleType) {
        this.vehichleType = vehichleType;
    }

    public String getDbcFileName() {
        return dbcFileName;
    }

    public void setDbcFileName(String dbcFileName) {
        this.dbcFileName = dbcFileName;
    }

    public String getDbcFastDFSFileId() {
        return dbcFastDFSFileId;
    }

    public void setDbcFastDFSFileId(String dbcFastDFSFileId) {
        this.dbcFastDFSFileId = dbcFastDFSFileId;
    }

    @Override
    public String toString() {
        return "CanVid2FileIdBean{" +
                "vid='" + vid + '\'' +
                ", vehichleType='" + vehichleType + '\'' +
                ", dbcFileName='" + dbcFileName + '\'' +
                ", dbcFastDFSFileId='" + dbcFastDFSFileId + '\'' +
                '}';
    }
}
