/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.agent.common.api;

/**
 * 通用返回对象
 *
 * @author h30009881
 * @param <T>
 * @since 2021-10-14
 */
public class CommonResult<T> {
    // 提示信息
    private String msg;

    // 数据封装
    private T data;

    // 总数
    private int code;

    protected CommonResult() {
    }

    protected CommonResult(String msg, T data, int code) {
        this.msg = msg;
        this.data = data;
        this.code = code;
    }

    /**
     * 成功返回结果
     *
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<T>(null, null, 0);
    }

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(null, data, 0);
    }

    /**
     * 失败返回结果
     *
     * @param msg 错误信息
     * @param <T>
     * @return {@link CommonResult}
     */
    public static <T> CommonResult<T> failed(String msg) {
        return new CommonResult<T>(msg, null, -1);
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
        return code;
    }

    public void setTotal(int code) {
        this.code = code;
    }
}
