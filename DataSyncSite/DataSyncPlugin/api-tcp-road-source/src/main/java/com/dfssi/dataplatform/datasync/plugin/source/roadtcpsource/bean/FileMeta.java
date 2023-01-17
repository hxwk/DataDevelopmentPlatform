package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.bean;

/*
 *
 * @Author chenf
 * @Description //TODO 文件上传
 * @Date  2018/9/13
 * @Param 
 * @return 
 **/
public class FileMeta {

    private String vid;
    private String sim;
    private Integer fileId;
    private Byte fileType;
    private int fileFormat;
    private String fileKey;
    private String filePath;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Byte getFileType() {
        return fileType;
    }

    public void setFileType(Byte fileType) {
        this.fileType = fileType;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public int getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(int fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public String toString() {
        return "MediaMeta{" +
                "vid=" + vid +
                ", sim=" + sim +
                ", fileId=" + fileId +
                ", fileType=" + fileType +
                ", fileFormat=" + fileFormat +
                ", fileKey='" + fileKey + '\'' +
                '}';
    }
}
