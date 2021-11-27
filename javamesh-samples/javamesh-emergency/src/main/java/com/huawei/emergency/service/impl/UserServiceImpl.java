package com.huawei.emergency.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.huawei.common.util.JwtTokenUtil;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.entity.User;
import com.huawei.emergency.mapper.UserMapper;
import com.huawei.emergency.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = mapper.selectUserByName(username);
        if (user != null) {
            List<String> auth = mapper.getAuthByRole(user.getRole());
            return new JwtUser(user, auth);
        }
        throw new UsernameNotFoundException("用户名或密码不存在");
    }
}