package com.huawei.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.huawei.user.common.api.CommonResult;
import com.huawei.user.common.constant.FailedInfo;
import com.huawei.user.entity.UserEntity;
import com.huawei.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@RestController()
@RequestMapping("/api")
public class UserController {
    private static final String SUCCESS = "success";

    @Autowired
    private UserService service;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.expiration}")
    private int expiration;

    /*@PostMapping("/user/login")
    public CommonResult login(HttpServletResponse response, @RequestBody JSONObject params) {
        String username = params.getString("username");
        String password = params.getString("password");
        String nativeLanguage = "cn";
        String userTimezone = "Asia/Shanghai";
        JSONObject jsonObject = service.login(username, password, nativeLanguage, userTimezone);
        if (jsonObject != null && Boolean.parseBoolean(jsonObject.get("success").toString())) {
            String sessionId = jsonObject.get("JSESSIONID").toString();
            Cookie cookie = new Cookie("JSESSIONID", sessionId);
            cookie.setMaxAge(12 * 60 * 60);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return CommonResult.success();
        } else {
            return CommonResult.failed("用户名或密码不存在");
        }
    }*/
    @PostMapping("/user/login")
    public CommonResult login(HttpServletResponse response, @RequestBody JSONObject params) {
        String username = params.getString("username");
        String password = params.getString("password");
        String token = service.login(username, password);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(expiration);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setStatus(200);
        //response.setHeader(tokenHeader,tokenHead+token);
        return CommonResult.success();
    }

    /*@GetMapping("/user/me")
    public CommonResult getUserInfo(HttpServletRequest request) {
        return service.getUserInfo(request);
    }*/

    @GetMapping("/user/me")
    public CommonResult getUserInfo(HttpServletResponse response, Principal principal) {
        if (principal == null) {
            response.setStatus(200);
            return CommonResult.failed(FailedInfo.GET_USER_FAILED);
        }
        return service.getUserInfo(response, principal.getName());
    }

    @PostMapping("/user/chagnePwd")
    public CommonResult changePwd(Principal principal, @RequestBody Map<String, String> param) {
        String result = service.changePwd(principal.getName(), param);
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
    public CommonResult register(@RequestBody UserEntity entity) {
        String result = service.register(entity);
        if (!result.equals(SUCCESS)) {
            return CommonResult.failed(result);
        }
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
                                 @RequestParam(value = "order", defaultValue = "DESC") String order) {
        return service.listUser(nickName, userName, role, status, pageSize, current, sorter, order);
    }

    @PostMapping("/user/batchDeactive")
    public CommonResult suspend(Principal principal, @RequestBody Map<String, String[]> param) {
        String[] usernames = param.get("username");
        String result = service.suspend(principal.getName(), usernames);
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
    public CommonResult updateUser(@RequestBody UserEntity user) {
        String result = service.updateUser(user);
        if (result.equals(SUCCESS)) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed(result);
        }
    }

}
