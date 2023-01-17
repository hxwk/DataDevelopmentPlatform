package com.dfssi.dataplatform.ide.security.entity;

import lombok.Data;


@Data
public class SecurityAuth {
    private int id;
    private String auth_name;
    private String auth_desc;
    private String auth_type;
    private String auth_user;
    private String auth_password;
    private String creator;
    private String editor;
    private String create_date;
    private String update_date;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAuth_name() {
        return auth_name;
    }

    public void setAuth_name(String auth_name) {
        this.auth_name = auth_name;
    }

    public String getAuth_desc() {
        return auth_desc;
    }

    public void setAuth_desc(String auth_desc) {
        this.auth_desc = auth_desc;
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreate_date() {
        if(create_date == null)return null;
        return create_date.endsWith(".0") ? create_date.substring(0, create_date.length() - 2) : create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getUpdate_date() {
        if(update_date == null) return  null;
        return update_date.endsWith(".0") ? update_date.substring(0, update_date.length() - 2) : update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getAuth_user() {
        return auth_user;
    }

    public void setAuth_user(String auth_user) {
        this.auth_user = auth_user;
    }

    public String getAuth_password() {
        return auth_password;
    }

    public void setAuth_password(String auth_password) {
        this.auth_password = auth_password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SecurityAuth{");
        sb.append("id=").append(id);
        sb.append(", auth_name='").append(auth_name).append('\'');
        sb.append(", auth_desc='").append(auth_desc).append('\'');
        sb.append(", auth_type='").append(auth_type).append('\'');
        sb.append(", auth_user='").append(auth_user).append('\'');
        sb.append(", auth_password='").append(auth_password).append('\'');
        sb.append(", creator='").append(creator).append('\'');
        sb.append(", create_date=").append(create_date);
        sb.append(", update_date=").append(update_date);
        sb.append('}');
        return sb.toString();
    }
}