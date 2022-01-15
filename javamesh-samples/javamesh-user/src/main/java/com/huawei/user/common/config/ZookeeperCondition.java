package com.huawei.user.common.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ZookeeperCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        String enabled = environment.getProperty("spring.cloud.zookeeper.auth.enabled");
        if("true".equals(enabled)){
            return true;
        }
        return false;
    }
}
