/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.common.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 通用返回对象
 *
 * @author h30009881
 * @param <T>
 * @since 2021-10-14
 */
public class CommonResult<T> {
    private int code;
    // 提示信息
    private String msg;

    // 数据封装
    private T data;

    // 总数
    private int total;

    protected CommonResult() {
    }

    protected CommonResult(int code, String msg, T data, int total) {
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
        return new CommonResult<T>(200, null, null, 0);
    }

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(200, null, data, 0);
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
        return new CommonResult<T>(200, null, data, total);
    }

    /**
     * 失败返回结果
     *
     * @param msg 错误信息
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> failed(String msg) {
        return new CommonResult<T>(500, msg, null, 0);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(500, message, null, 0);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return code == 200;
    }
}
