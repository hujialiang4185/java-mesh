
package com.huawei.user.common.config;

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

@SpringBootConfiguration
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
        addAuthInfo(builder, username, password);
        if (this.ensembleProvider != null) {
            builder.ensembleProvider(this.ensembleProvider);
        } else {
            builder.connectString(properties.getConnectString());
        }

        CuratorFramework curator = builder.retryPolicy(retryPolicy).build();
        curator.start();
        log.trace("blocking until connected to zookeeper for " + properties.getBlockUntilConnectedWait() + properties.getBlockUntilConnectedUnit());
        curator.blockUntilConnected(properties.getBlockUntilConnectedWait(), properties.getBlockUntilConnectedUnit());
        log.trace("connected to zookeeper");
        return curator;
    }


/**
     * 添加授权
     *
     * @param builder
     * @param username
     * @param password
     * @return
     */

    private CuratorFrameworkFactory.Builder addAuthInfo(CuratorFrameworkFactory.Builder builder, String username, String password) {
        log.info("zookeeper.username={}", username);
        log.info("zookeeper.password={}", password);
        if (org.apache.commons.lang.StringUtils.isNotBlank(username) && org.apache.commons.lang.StringUtils.isNotBlank(password)) {
            String authInfo = username + ":" + password;
            builder.authorization("digest", authInfo.getBytes());
        } else {
            log.info("Cannot resolve zookeeper username or password.");
        }
        return builder;
    }
}

