package com.huawei.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.huawei.user.common.api.CommonResult;
import com.huawei.user.common.constant.FailedInfo;
import com.huawei.user.entity.JwtUser;
import com.huawei.user.entity.UserEntity;
import com.huawei.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private static final String SUCCESS = "success";

    @Autowired
    private UserService service;

    @PostMapping("/user/login")
    public CommonResult login(HttpServletResponse response, @RequestBody JSONObject params) {
        String username = params.getString("username");
        String password = params.getString("password");
        return service.login(response,username, password);
    }

    @GetMapping("/user/me")
    public CommonResult getUserInfo(HttpServletResponse response, UsernamePasswordAuthenticationToken authentication) {
        if (authentication == null) {
            response.setStatus(200);
            return CommonResult.failed(FailedInfo.GET_USER_FAILED);
        }
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        return service.getUserInfo(response, jwtUser);
    }

    @PostMapping("/user/chagnePwd")
    public CommonResult changePwd(UsernamePasswordAuthenticationToken authentication, @RequestBody Map<String, String> param) {
        UserEntity user = ((JwtUser) authentication.getPrincipal()).getUserEntity();
        String result = service.changePwd(user, param);
        if (!result.equals(SUCCESS)) {
            return CommonResult.failed(result);
        }
        return CommonResult.success(result);
    }

    @PostMapping("/user/logout")
    public CommonResult logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setStatus(200);
        return CommonResult.success();
    }

    @PostMapping("/user/registe")
    public CommonResult register(HttpServletResponse response,@RequestBody UserEntity entity) {
        String result = service.register(entity);
        if (!result.equals(SUCCESS)) {
            return CommonResult.failed(result);
        }
        response.setStatus(200);
        return CommonResult.success(result);
    }

    @GetMapping("/user")
    public CommonResult listUser(@RequestParam(value = "nickname", required = false) String nickName,
                                 @RequestParam(value = "username", required = false) String userName,
                                 @RequestParam(value = "role", required = false) String role,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                 @RequestParam(value = "current", defaultValue = "1") int current,
                                 @RequestParam(value = "sorter", defaultValue = "created_date") String sorter,
                                 @RequestParam(value = "order", required = false) String order) {
        return service.listUser(nickName, userName, role, status, pageSize, current, sorter, order);
    }

    @PostMapping("/user/batchDeactive")
    public CommonResult suspend(UsernamePasswordAuthenticationToken authentication, @RequestBody Map<String, String[]> param) {
        String result = service.suspend(((JwtUser) authentication.getPrincipal()).getUserEntity(), param.get("username"));
        if (result.equals(SUCCESS)) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result);
        }
    }

    @PostMapping("/user/batchActive")
    public CommonResult enable(@RequestBody Map<String, String[]> param) {
        String[] usernames = param.get("username");
        String result = service.enable(usernames);
        if (result.equals(SUCCESS)) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result);
        }
    }

    @PostMapping("/user")
    public CommonResult addUser(@RequestBody UserEntity user) {
        return service.addUser(user);
    }

    @PostMapping("/user/resetPwd")
    public CommonResult resetPwd(@RequestBody UserEntity user) {
        return service.resetPwd(user);
    }

    @PutMapping("/user")
    public CommonResult updateUser(UsernamePasswordAuthenticationToken authentication,@RequestBody UserEntity user) {
        String result = service.updateUser(((JwtUser) authentication.getPrincipal()).getUserEntity(),user);
        if (result.equals(SUCCESS)) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result);
        }
    }

    @GetMapping("/user/approver/search")
    public CommonResult approverSearch(@RequestParam(value = "group_id", required = false) String groupId, UsernamePasswordAuthenticationToken authentication) {
        return service.approverSearch(groupId,(JwtUser) authentication.getPrincipal());
    }
}
