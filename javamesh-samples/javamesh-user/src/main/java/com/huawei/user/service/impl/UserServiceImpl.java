package com.huawei.user.service.impl;

import com.huawei.user.common.api.CommonResult;
import com.huawei.user.common.constant.FailedInfo;
import com.huawei.user.common.util.EscapeUtil;
import com.huawei.user.common.util.JwtTokenUtil;
import com.huawei.user.entity.JwtUser;
import com.huawei.user.entity.UserEntity;
import com.huawei.user.mapper.UserMapper;
import com.huawei.user.service.UserService;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
@Transactional
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

    @Autowired
    UserMapper mapper;

    @Value("${jwt.expiration}")
    private int expiration;

    @Override
    public CommonResult login(HttpServletResponse response, String username, String password) {
        try {
            response.setStatus(200);
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                return CommonResult.failed("登录账号或密码错误");
            }
            if (!userDetails.isEnabled()) {
                return CommonResult.failed("账号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtil.generateToken(userDetails);
            if (token == null) {
                return CommonResult.failed("用户名或密码错误");
            }
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(expiration);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return CommonResult.success();
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
            return CommonResult.failed("登录失败");
        }
    }

    @Override
    public CommonResult getUserInfo(HttpServletResponse response, JwtUser jwtUser) {
        UserEntity user = jwtUser.getUserEntity();
        user.setAuth(jwtUser.getAuthList());
        String role = user.getRole();
        switch (role) {
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

    @Override
    public String changePwd(UserEntity user, Map<String, String> param) {
        String oldPassword = param.get("old_password");
        String password = param.get("password");
        String confirm = param.get("confirm");
        if (oldPassword.equals(password)) {
            return FailedInfo.OLD_NEW_PASSWORD_EQUALS;
        } else if (!password.equals(confirm)) {
            return FailedInfo.CONFIRM_PASSWORD_ERROR;
        } else {
            // 原密码错误返回信息
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return FailedInfo.PASSWORD_ERROR;
            }

            // 修改密码
            int count = mapper.updatePwdByName(user.getUserName(), passwordEncoder.encode(password), getTimestamp());
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

        // 插入emergency_user
        count = mapper.insertUserToEmergency(entity);
        if (count <= 0) {
            return FailedInfo.REGISTER_FAILED;
        }
        entity.setRole("USER");

        // 插入nuser
        count = mapper.insertUser(entity);
        if (count == 1) {
            return SUCCESS;
        }
        return FailedInfo.REGISTER_FAILED;
    }

    @Override
    public CommonResult listUser(UserEntity user, int pageSize, int current, String sorter, String order) {
        String mSorter = sorter.equals("update_time") ? "last_modified_date" : sorter;
        String sortType;
        if (StringUtils.isBlank(order)) {
            sortType = "created_date" + System.lineSeparator() + "DESC";
        } else if (order.equals("ascend")) {
            sortType = mSorter + System.lineSeparator() + "ASC";
        } else {
            sortType = mSorter + System.lineSeparator() + "DESC";
        }
        Page<UserEntity> pageInfo = PageHelper.startPage(current, pageSize, sortType).doSelectPage(() -> {
            mapper.listUser(user);
        });
        List<UserEntity> users = pageInfo.getResult();
        return CommonResult.success(users, (int) pageInfo.getTotal());
    }

    @Override
    public String suspend(UserEntity user, String[] usernames) {
        String userName = user.getUserName();
        for (String name : usernames) {
            if (name.equals(userName) || name.equals("admin")) {
                return FailedInfo.SUSPEND_NOT_SELF_OR_ADMIN;
            }
        }
        int count = mapper.updateEnableByName(usernames, "F", getTimestamp());
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
        int count = mapper.updateEnableByName(usernames, "T", getTimestamp());
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
        user.setPassword(passwordEncoder.encode(password));
        String role = user.getRole();

        Timestamp timestamp = getTimestamp();
        user.setCreateTime(timestamp);
        user.setUpdateTime(timestamp);
        user.setEnabled("T");
        setRoleToUser(user, role);

        // 插入emergency_user表
        count = mapper.insertUserToEmergency(user);
        if (count != 1) {
            return CommonResult.failed(FailedInfo.ADD_USER_FAIL);
        }

        // 插入nuser表，将角色改为USER
        user.setRole("USER");
        count = mapper.insertUser(user);
        user.setPassword(password);
        if (count == 1) {
            return CommonResult.success(user);
        } else {
            return CommonResult.failed(FailedInfo.ADD_USER_FAIL);
        }
    }

    private void setRoleToUser(UserEntity user, String role) {
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

    @Override
    public CommonResult resetPwd(UserEntity user) {
        String userName = user.getUserName();
        String password = generatePassword();
        user.setPassword(password);
        Timestamp timestamp = getTimestamp();
        int count = mapper.updatePwdByName(userName, passwordEncoder.encode(password), timestamp);
        user.setUpdateTime(timestamp);
        if (count == 1) {
            return CommonResult.success(user);
        } else {
            return CommonResult.failed(FailedInfo.RESET_PWD_FAIL);
        }
    }

    @Override
    public String updateUser(UserEntity loginUser, UserEntity user) {
        String userName = user.getUserName();
        String group = mapper.getGroupByUser(userName);
        if (StringUtils.isNotBlank(group)) {
            if (!loginUser.getUserName().equals("admin") && !group.equals(loginUser.getGroupName())) {
                return FailedInfo.CANNOT_UPDATE_OTHER_GROUP_USERS;
            }
        }
        if (userName.equals("admin")) {
            return FailedInfo.CANNOT_UPDATE_ADMIN;
        }
        String role = user.getRole();
        setRoleToUser(user, role);
        user.setUpdateTime(getTimestamp());
        int count = mapper.updateEmergencyUser(user);
        if (count != 1) {
            return FailedInfo.UPDATE_USER_FAIL;
        }
        user.setRole("USER");
        count = mapper.updateUser(user);
        if (count == 1) {
            return SUCCESS;
        }
        return FailedInfo.UPDATE_USER_FAIL;
    }

    @Override
    public CommonResult approverSearch(String groupId, JwtUser jwtUser) {
        UserEntity user = jwtUser.getUserEntity();
        String userName = user.getUserName();
        if (userName.equals("admin")) {
            String[] result = {userName};
            return CommonResult.success(result);
        }
        List<String> userEntities = mapper.adminApproverSearch(user.getGroupName());
        return CommonResult.success(userEntities);
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

    private Timestamp getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = sdf.format(new Date());
        return Timestamp.valueOf(nowDate);
    }
}
