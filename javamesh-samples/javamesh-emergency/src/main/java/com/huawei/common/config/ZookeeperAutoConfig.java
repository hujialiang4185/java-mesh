/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.common.config;

import lombok.extern.slf4j.Slf4j;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.zookeeper.ZookeeperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.nio.charset.StandardCharsets;

/**
 * zookeeper鉴权配置
 *
 * @author h30009881
 * @since 2021-10-30
 */
@SpringBootConfiguration
@Conditional(ZookeeperCondition.class)
@Slf4j
public class ZookeeperAutoConfig {
    @Value("${spring.cloud.zookeeper.auth.username}")
    private String username;
    @Value("${spring.cloud.zookeeper.auth.password}")
    private String password;

    @Autowired(required = false)
    private EnsembleProvider ensembleProvider;

    @Bean
    public CuratorFramework curatorFramework(RetryPolicy retryPolicy, ZookeeperProperties properties) throws Exception {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        addAuthInfo(builder);
        if (this.ensembleProvider != null) {
            builder.ensembleProvider(this.ensembleProvider);
        } else {
            builder.connectString(properties.getConnectString());
        }

        CuratorFramework curator = builder.retryPolicy(retryPolicy).build();
        curator.start();
        log.trace("blocking until connected to zookeeper for " + properties.getBlockUntilConnectedWait()
            + properties.getBlockUntilConnectedUnit());
        curator.blockUntilConnected(properties.getBlockUntilConnectedWait(), properties.getBlockUntilConnectedUnit());
        log.trace("connected to zookeeper");
        return curator;
    }

    /**
     * 添加授权
     *
     * @param builder
     * @return
     */

    private CuratorFrameworkFactory.Builder addAuthInfo(CuratorFrameworkFactory.Builder builder) {
        log.info("zookeeper.username={}", username);
        log.info("zookeeper.password={}", password);
        if (org.apache.commons.lang.StringUtils.isNotBlank(username) && org.apache.commons.lang.StringUtils.isNotBlank(
            password)) {
            String authInfo = username + ":" + password;
            builder.authorization("digest", authInfo.getBytes(StandardCharsets.UTF_8));
        } else {
            log.info("Cannot resolve zookeeper username or password.");
        }
        return builder;
    }
}
