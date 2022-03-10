/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.logaudit.aop;


import com.huawei.logaudit.constant.OperationTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 记录接口操作日志的切面
 *
 * @author zhengbin zhao
 * @since 2021-03-09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebOperationLog {
    /**
     * 资源类型（模块名称）
     *
     * @return 资源模块实体
     */
    String resourceType() default "";

    /**
     * 操作类型
     *
     * @return 操作类型枚举
     */
    OperationTypeEnum operationType() default OperationTypeEnum.UNKNOW;

    /**
     * 操作详情
     *
     * @return 操作详情实体
     */
    String operationDetails() default "";
}
