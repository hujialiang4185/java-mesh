package com.huawei.common.filter;

import com.alibaba.fastjson.JSONObject;
import com.huawei.common.util.UserFeignClient;
import com.huawei.emergency.entity.User;
import com.huawei.emergency.mapper.UserMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebFilter(urlPatterns = {"/*"})
@Slf4j
public class UserFilter implements Filter {
    @Resource
    private UserFeignClient userFeignClient;

    private static final ThreadLocal<User> users = new ThreadLocal<>();

    @Autowired
    private UserMapper mapper;

    private HttpSession session;

    private User user;

    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/ws")));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        if(!ALLOWED_PATHS.contains(path)){
            try {
                JSONObject userInfo = userFeignClient.getUserInfo();
                session = request.getSession();
                String userId = (String)userInfo.get("userId");
                String role = mapper.getRoleByUserName(userId);
                List<String> auth = mapper.getAuthByRole(role);
                user = new User(userId,(String)userInfo.get("userName"),role,auth);
                session.setAttribute("userInfo", user);
                users.set(user);
            } catch (FeignException e) {
                log.error("No login. ");
                response.setStatus(401);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    public static String currentUserName() {
        User user = users.get();
        if (user == null) {
            return null;
        }
        return user.getNickName();
    }
}
