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

package com.huawei.common.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用返回对象
 *
 * @param <T>
 * @author h30009881
 * @since 2021-10-14
 */
@NoArgsConstructor
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
     * @param data 获取的数据
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
