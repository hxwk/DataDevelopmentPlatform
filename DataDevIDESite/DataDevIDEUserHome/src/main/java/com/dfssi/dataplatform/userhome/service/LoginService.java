package com.dfssi.dataplatform.userhome.service;

import com.dfssi.dataplatform.userhome.entity.UserEntity;

public interface LoginService {
    UserEntity login(String uName, String uPsword);
}