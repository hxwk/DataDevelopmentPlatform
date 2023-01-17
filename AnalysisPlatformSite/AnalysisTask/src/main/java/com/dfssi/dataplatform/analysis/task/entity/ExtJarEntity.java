package com.dfssi.dataplatform.analysis.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ExtJarEntity {

    private int jarId;
    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String uploadDate;
    private String uploadUser;
    private long jarSize;
    private String jarPath;
    private int isValid;

    public ExtJarEntity() {
    }

    public ExtJarEntity(String name,
                        String description,
                        String uploadDate,
                        String uploadUser,
                        long jarSize,
                        String jarPath,
                        int isValid) {
        this.name = name;
        this.description = description;
        this.uploadDate = uploadDate;
        this.uploadUser = uploadUser;
        this.jarSize = jarSize;
        this.jarPath = jarPath;
        this.isValid = isValid;
    }

    public int getJarId() {
        return jarId;
    }

    public void setJarId(int jarId) {
        this.jarId = jarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public long getJarSize() {
        return jarSize;
    }

    public void setJarSize(long jarSize) {
        this.jarSize = jarSize;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

}
