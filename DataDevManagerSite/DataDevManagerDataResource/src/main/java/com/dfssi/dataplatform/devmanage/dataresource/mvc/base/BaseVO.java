package com.dfssi.dataplatform.devmanage.dataresource.mvc.base;

/**
 * Created by cxq on 2018/1/3.
 */
public class BaseVO {
    private Integer isValid;
    private String createDate;
    private String createUser;
    private String updateDate;
    private String updateUser;

    private int curPage;
    private int pageLength;

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public int getCurPage() {
        if(curPage==0)curPage=1;
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageLength() {
        if(pageLength==0)pageLength=5;
        return pageLength;
    }

    public void setPageLength(int pageLength) {
        this.pageLength = pageLength;
    }
}
