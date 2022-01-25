/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 *
 * @since 2021-10-30
 **/
@SpringBootApplication(scanBasePackages = {"com.huawei", "org.ngrinder"})
@EnableFeignClients
@EnableDiscoveryClient
@ServletComponentScan
@MapperScan(basePackages = {"com.huawei.emergency.mapper"})
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = {"org.ngrinder", "com.huawei"})
@EntityScan(basePackages = {"org.ngrinder", "com.huawei"})
@EnableCaching
public class EmergencyDrillApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmergencyDrillApplication.class, args);
    }
}
