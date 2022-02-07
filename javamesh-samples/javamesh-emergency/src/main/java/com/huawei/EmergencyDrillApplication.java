/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei;

import com.huawei.argus.restcontroller.RestFileEntryController;
import org.mybatis.spring.annotation.MapperScan;
import org.ngrinder.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

/**
 * 启动类
 *
 * @since 2021-10-30
 **/
@SpringBootApplication(scanBasePackages = {"com.huawei"})
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
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyDrillApplication.class);

    public static void main(String[] args) {
        LOGGER.info("start >>>>>>>>>>>>");
        ConfigurableApplicationContext run = SpringApplication.run(EmergencyDrillApplication.class, args);
        final RestFileEntryController bean = run.getBean(RestFileEntryController.class);
        User user = new User();
        user.setUserId("admin");
        Map<String, Object> test = bean.addFolder(user, "/", "test");
        LOGGER.info("xxxx" + test);
        System.out.println(test);
        LOGGER.info("success");
        Logger logger = LoggerFactory.getLogger(EmergencyDrillApplication.class);
    }
}
