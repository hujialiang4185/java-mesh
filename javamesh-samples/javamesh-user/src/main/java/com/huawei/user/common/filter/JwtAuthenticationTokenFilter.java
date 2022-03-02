package com.huawei.user.common.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.huawei.user.common.constant.FailedInfo;
import com.huawei.user.common.util.JwtTokenUtil;
import com.huawei.user.entity.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * JWT登录授权过滤器
 * Created by macro on 2018/4/26.
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/api/user/login", "/api/user/registe", "/api/user/me", "/api/user/logout")));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String token = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    break;
                }
            }
            if (token != null) {
                String username = jwtTokenUtil.getUserNameFromToken(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
                        if (!ALLOWED_PATHS.contains(path)) {
                            if (!"admin".equals(jwtUser.getUsername()) && StringUtils.isBlank(jwtUser.getGroupName())) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("msg", FailedInfo.USER_HAVE_NOT_GROUP);
                                responseJson(response, jsonObject);
                                return;
                            }
                        }
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        LOGGER.info("authenticated user:{}", username);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {
                    response.setStatus(401);
                }
            } else {
                response.setStatus(401);
            }
        } else {
            response.setStatus(401);
        }
        chain.doFilter(request, response);
    }

    private void responseJson(HttpServletResponse response, Object obj) {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat));
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Exception occurs. Exception info {}", e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
