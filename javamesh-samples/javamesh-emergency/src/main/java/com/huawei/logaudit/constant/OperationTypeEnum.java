/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.logaudit.constant;

/**
 * 操作类型和级别的枚举类
 * 级别：
 * 1-提示
 * 2-一般
 * 3-警告
 * 4-危险
 * 5-高危
 *
 * @author zhengbin zhao
 * @since 2021-03-09
 */
public enum OperationTypeEnum {
    /**
     * 操作类型：增加；级别：一般
     */
    CREATE("增加", 2),

    /**
     * 操作类型：下载；级别：提示
     */
    DOWNLOAD("下载", 1),

    /**
     * 操作类型：上传；级别：一般
     */
    UPLOAD("上传", 2),

    /**
     * 操作类型：登录；级别：提示
     */
    LOGIN("登录", 1),

    /**
     * 操作类型：注销；级别：提示
     */
    LOGINOUT("注销", 1),

    /**
     * 操作类型：删除；级别：高危
     */
    DELETE("删除", 5),

    /**
     * 操作类型：修改；级别：高危
     */
    UPDATE("修改", 5),

    /**
     * 操作类型：查询；级别：提示
     */
    SELECT("查询", 1),

    /**
     * 操作类型：提审；级别：提示
     */
    SUBMIT_REVIEW("提审", 1),

    /**
     * 操作类型：审核；级别：提示
     */
    AUDIT("审核", 1),

    /**
     * 操作类型：调试；级别：提示
     */
    DEBUG("调试", 1),

    /**
     * 操作类型：中止调试；级别：提示
     */
    DEBUG_STOP("中止调试", 1),

    /**
     * 操作类型：调试；级别：提示
     */
    DEBUG_LOG("调试日志", 1),

    /**
     * 操作类型：未知；级别：未知
     */
    UNKNOW("未知", 10000);

    private String typeString;
    private int typeInt;

    OperationTypeEnum(String typeString, int typeInt) {
        this.typeString = typeString;
        this.typeInt = typeInt;
    }

    public String getTypeString() {
        return typeString;
    }

    public int getTypeInt() {
        return typeInt;
    }
}
