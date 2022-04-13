/*
 * Copyright (C) Ltd. 2022-2022. Huawei Technologies Co., All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.common.filter;

import com.huawei.argus.config.UserFactory;
import com.huawei.common.constant.FailedInfo;
import com.huawei.common.util.JwtTokenUtil;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.entity.UserEntity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.ngrinder.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt登录过滤器
 *
 * @author h30009881
 * @since 2022-01-01
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
        Arrays.asList("/ws", "/swagger-ui.html", "/api/script/execComplete")));
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            chain.doFilter(request, response);
            return;
        }
        String token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
                break;
            }
        }
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            chain.doFilter(request, response);
            return;
        }
        String username = jwtTokenUtil.getUserNameFromToken(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (!jwtTokenUtil.validateToken(token, userDetails)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                chain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
            if (!"T".equals(jwtUser.getUserEntity().getEnabled())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (!ALLOWED_PATHS.contains(path)) {
                if (!"admin".equals(jwtUser.getUsername()) && StringUtils.isBlank(jwtUser.getGroupName())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", FailedInfo.USER_HAVE_NOT_GROUP);
                    responseJson(response, jsonObject);
                    return;
                }
            }
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            LOGGER.debug("authenticated user:{}", username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

    public static org.ngrinder.model.User currentGrinderUser() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken)
            SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = ((JwtUser) authentication.getPrincipal()).getUserEntity();
        if (userEntity == null) {
            log.error("schedule task with no login.");
            return UserFactory.newInstance();
        }
        org.ngrinder.model.User grinderUser = new org.ngrinder.model.User();
        grinderUser.setUserName(userEntity.getNickName());
        grinderUser.setRole(Role.ADMIN);
        grinderUser.setUserLanguage("en");
        grinderUser.setId(userEntity.getUserId());
        grinderUser.setUserId(userEntity.getUserName());
        grinderUser.setTimeZone("Asia/Shanghai");
        grinderUser.setExternal(true);
        grinderUser.setEnabled(true);
        return grinderUser;
    }
}
