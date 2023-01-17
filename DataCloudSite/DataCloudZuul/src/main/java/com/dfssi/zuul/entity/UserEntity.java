package com.dfssi.zuul.entity;

import java.io.Serializable;
import java.util.Date;

public class UserEntity implements Serializable {
    public String getuRoleName() {
        return uRoleName;
    }

    public void setuRoleName(String uRoleName) {
        this.uRoleName = uRoleName;
    }
    private String uRoleName;
    private String id;

    private String name;

    private String uName;

    private String uPsword;

    private Date createTime;

    private Integer isDelete;

    private Integer uType;

    private String uRole;

    private String orgId;

    private String telephone;

    private String site;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName == null ? null : uName.trim();
    }

    public String getuPsword() {
        return uPsword;
    }

    public void setuPsword(String uPsword) {
        this.uPsword = uPsword == null ? null : uPsword.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getuType() {
        return uType;
    }

    public void setuType(Integer uType) {
        this.uType = uType;
    }

    public String getuRole() {
        return uRole;
    }

    public void setuRole(String uRole) {
        this.uRole = uRole == null ? null : uRole.trim();
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site == null ? null : site.trim();
    }
}