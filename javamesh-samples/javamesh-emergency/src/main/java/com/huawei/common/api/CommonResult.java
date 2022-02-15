/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.common.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 通用返回对象
 *
 * @param <T>
 * @author h30009881
 * @since 2021-10-14
 */
@Data
public class CommonResult<T> {
    private static final int SUCCESS = 200;
    private static final int FAILED = 500;

    private int code;
    private String msg;
    private T data;
    private int total;

    private CommonResult(int code, String msg, T data, int total) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.total = total;
    }

    /**
     * 成功返回结果
     *
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<T>(SUCCESS, null, null, 0);
    }

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(SUCCESS, null, data, 0);
    }

    /**
     * 成功返回结果
     *
     * @param data  获取的数据
     * @param total 数据总数
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> success(T data, int total) {
        return new CommonResult<T>(SUCCESS, null, data, total);
    }

    /**
     * 失败返回结果
     *
     * @param msg 错误信息
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> failed(String msg) {
        return new CommonResult<T>(FAILED, msg, null, 0);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(FAILED, message, null, 0);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return code == SUCCESS;
    }
}
