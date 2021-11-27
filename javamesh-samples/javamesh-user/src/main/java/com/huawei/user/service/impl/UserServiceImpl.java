package com.huawei.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.huawei.user.common.api.CommonResult;
import com.huawei.user.common.constant.FailedInfo;
import com.huawei.user.common.exception.Asserts;
import com.huawei.user.common.util.JwtTokenUtil;
import com.huawei.user.common.util.PageUtil;
import com.huawei.user.common.util.UserFeignClient;
import com.huawei.user.entity.JwtUser;
import com.huawei.user.entity.UserEntity;
import com.huawei.user.mapper.UserMapper;
import com.huawei.user.service.UserService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private static final String SUCCESS = "success";

    private static final String ROLE_ADMIN = "管理员";

    private static final String ROLE_OPERATOR = "操作员";

    private static final String ROLE_APPROVER = "审核员";

    private static final String HEALTHY = "正常";

    private static final String EXPIRED = "失效";
    private static final int PASSWORD_LENGTH = 10;

    private static final String PASSWORD_DIRECTORY = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Resource
    UserFeignClient userFeignClient;

    @Autowired
    UserMapper mapper;

    @Override
    public JSONObject login(String username, String password, String nativeLanguage, String userTimezone) {
        return userFeignClient.login(username, password, nativeLanguage, userTimezone);
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                Asserts.fail("密码不正确");
            }
            if (!userDetails.isEnabled()) {
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public CommonResult getUserInfo(HttpServletResponse response,String userName) {
        UserEntity user = mapper.selectUserByName(userName);
        if (user != null) {
            List<String> auth = mapper.getAuthByRole(user.getRole());
            user.setAuth(auth);
            switch (user.getRole()){
                case "ADMIN":
                    user.setRole(ROLE_ADMIN);
                    break;
                case "APPROVER":
                    user.setRole(ROLE_APPROVER);
                    break;
                case "OPERATOR":
                    user.setRole(ROLE_OPERATOR);
            }
            return CommonResult.success(user);
        }
        return CommonResult.failed(FailedInfo.GET_USER_FAILED);
    }

    /*@Override
    public String logout() {
        return userFeignClient.logout();
    }*/

    @Override
    public String changePwd(String userName, Map<String, String> param) {
        String oldPassword = param.get("old_password");
        String password = param.get("password");
        String confirm = param.get("confirm");
        if (oldPassword.equals(password)) {
            return FailedInfo.OLD_NEW_PASSWORD_EQUALS;
        } else if (!password.equals(confirm)) {
            return FailedInfo.CONFIRM_PASSWORD_ERROR;
        } else {
            UserEntity user = mapper.selectUserByName(userName);

            // 原密码错误返回信息
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return FailedInfo.PASSWORD_ERROR;
            }

            // 修改密码
            int count = mapper.changePassword(userName, passwordEncoder.encode(password), getTimestamp());
            if (count != 1) {
                return FailedInfo.CHANGE_PASSWORD_FAILED;
            }
            return SUCCESS;
        }
    }

    @Override
    public String register(UserEntity entity) {
        String userName = entity.getUserName();
        int count = mapper.countByName(userName);
        if (count > 0) {
            return FailedInfo.USERNAME_EXISTS;
        }
        String password = passwordEncoder.encode(entity.getPassword());
        entity.setPassword(password);
        String role = entity.getRole();
        switch (role) {
            case ROLE_OPERATOR:
                entity.setRole("OPERATOR");
                break;
            case ROLE_APPROVER:
                entity.setRole("APPROVER");
        }
        Timestamp timestamp = getTimestamp();
        entity.setCreateTime(timestamp);
        entity.setUpdateTime(timestamp);
        entity.setEnabled("T");
        count = mapper.insertUser(entity);
        if (count == 1) {
            return SUCCESS;
        }
        return FailedInfo.REGISTER_FAILED;
    }

    @Override
    public CommonResult listUser(String nickName, String userName, String role, String status, int pageSize, int current, String sorter, String order) {
        UserEntity user = new UserEntity();
        user.setNickName(nickName);
        user.setUserName(userName);
        if (StringUtils.isNotBlank(role)) {
            switch (role) {
                case ROLE_OPERATOR:
                    user.setRole("OPERATOR");
                    break;
                case ROLE_APPROVER:
                    user.setRole("APPROVER");
                    break;
                case ROLE_ADMIN:
                    user.setRole("ADMIN");
            }
        }
        if (StringUtils.isNotBlank(status)) {
            switch (status) {
                case HEALTHY:
                    user.setEnabled("T");
                    break;
                case EXPIRED:
                    user.setEnabled("F");
            }
        }
        String mSorter = sorter.equals("update_time") ? "last_modified_date" : sorter;
        if (order.equals("ascend")) {
            PageHelper.orderBy(mSorter + System.lineSeparator() + "ASC");
        } else {
            PageHelper.orderBy(mSorter + System.lineSeparator() + "DESC");
        }
        List<UserEntity> users = mapper.listUser(user);
        return CommonResult.success(PageUtil.startPage(users, current, pageSize), users.size());
    }

    @Override
    public String suspend(String userName, String[] usernames) {
        for (String name : usernames) {
            if (name.equals(userName) || name.equals("admin")) {
                return FailedInfo.SUSPEND_NOT_SELF_OR_ADMIN;
            }
        }
        Timestamp timestamp = getTimestamp();
        int count = mapper.updateEnableByName(usernames, "F", timestamp);
        int length = usernames.length;
        if (count == length) {
            return SUCCESS;
        } else if (count == 0) {
            return FailedInfo.SUSPEND_FAIL;
        } else {
            return FailedInfo.SUSPEND_NOT_ALL_SUCCESS;
        }

    }

    @Override
    public String enable(String[] usernames) {
        Timestamp timestamp = getTimestamp();
        int count = mapper.updateEnableByName(usernames, "T", timestamp);
        int length = usernames.length;
        if (count == length) {
            return SUCCESS;
        } else if (count == 0) {
            return FailedInfo.ENABLE_FAIL;
        } else {
            return FailedInfo.ENABLE_NOT_ALL_SUCCESS;
        }
    }

    @Override
    public CommonResult addUser(UserEntity user) {
        String userName = user.getUserName();
        int count = mapper.countByName(userName);
        if (count > 0) {
            return CommonResult.failed(FailedInfo.USERNAME_EXISTS);
        }
        String password = generatePassword();
        String encodePassword = passwordEncoder.encode(password);
        user.setPassword(encodePassword);
        String role = user.getRole();
        switch (role) {
            case ROLE_OPERATOR:
                user.setRole("OPERATOR");
                break;
            case ROLE_APPROVER:
                user.setRole("APPROVER");
                break;
            case ROLE_ADMIN:
                user.setRole("ADMIN");
        }
        Timestamp timestamp = getTimestamp();
        user.setCreateTime(timestamp);
        user.setUpdateTime(timestamp);
        user.setEnabled("T");
        count = mapper.insertUser(user);
        user.setPassword(password);
        if (count == 1) {
            return CommonResult.success(user);
        } else {
            return CommonResult.failed(FailedInfo.ADD_USER_FAIL);
        }
    }

    @Override
    public CommonResult resetPwd(UserEntity user) {
        String userName = user.getUserName();
        String password = generatePassword();
        user.setPassword(password);
        int count = mapper.changePassword(userName, passwordEncoder.encode(password), getTimestamp());
        if (count == 1) {
            return CommonResult.success(user);
        } else {
            return CommonResult.failed(FailedInfo.RESET_PWD_FAIL);
        }
    }

    @Override
    public String updateUser(UserEntity user) {
        String userName = user.getUserName();
        if (userName.equals("admin")) {
            return FailedInfo.CANNOT_UPDATE_ADMIN;
        }
        String role = user.getRole();
        switch (role) {
            case ROLE_OPERATOR:
                user.setRole("OPERATOR");
                break;
            case ROLE_APPROVER:
                user.setRole("APPROVER");
                break;
            case ROLE_ADMIN:
                user.setRole("ADMIN");
        }
        user.setUpdateTime(getTimestamp());
        int count = mapper.updateUser(user);
        if (count == 1) {
            return SUCCESS;
        }
        return FailedInfo.UPDATE_USER_FAIL;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = mapper.selectUserByName(username);
        if (user != null) {
            List<String> auth = mapper.getAuthByRole(user.getRole());
            return new JwtUser(user, auth);
        }
        throw new UsernameNotFoundException("用户名或密码不存在");
    }

    private String generatePassword() {
        char chars[] = PASSWORD_DIRECTORY.toCharArray();
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(chars[r.nextInt(chars.length)]);
        }
        return sb.toString();
    }

    private String encodePassword(String userName, String password) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userName);
        jsonObject.put("password", password);
        String result = userFeignClient.encodePassword(jsonObject);
        if (result.startsWith("\"") && result.endsWith("\"")) {
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }

    private Timestamp getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = sdf.format(new Date());
        return Timestamp.valueOf(nowDate);
    }
}
