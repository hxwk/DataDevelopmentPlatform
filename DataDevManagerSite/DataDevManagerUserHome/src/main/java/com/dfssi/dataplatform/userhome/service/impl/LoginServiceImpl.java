package com.dfssi.dataplatform.userhome.service.impl;

import com.dfssi.dataplatform.userhome.Entity.UserEntity;
import com.dfssi.dataplatform.userhome.service.LoginService;
import com.dfssi.dataplatform.userhome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserService userService;
    @Override
    public UserEntity login(String uName, String uPsword) {
        UserEntity userEntity = userService.login(uName,uPsword);
        return userEntity;
    }

}