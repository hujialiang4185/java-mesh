package com.huawei.user.service;

import com.huawei.user.common.api.CommonResult;
import com.huawei.user.entity.JwtUser;
import com.huawei.user.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface UserService {
    CommonResult getUserInfo(HttpServletResponse response, JwtUser jwtUser);

    String changePwd(UserEntity user,Map<String, String> param);

    String register(UserEntity entity);

    CommonResult listUser(UserEntity user, int pageSize, int current, String sorter, String order);

    String suspend(UserEntity user, String[] usernames);

    String enable(String[] usernames);

    CommonResult addUser(UserEntity user);

    CommonResult resetPwd(UserEntity user);

    String updateUser(UserEntity loginUser,UserEntity user);

    CommonResult approverSearch(String groupId, JwtUser jwtUser);

    UserDetails loadUserByUsername(String username);

    CommonResult login(HttpServletResponse response,String username, String password);
}
