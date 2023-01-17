package com.dfssi.dataplatform.userhome.service;

import com.dfssi.dataplatform.userhome.Entity.UserEntity;

public interface LoginService {
    UserEntity login(String uName, String uPsword);
}