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

package com.huawei.agent.common.api;

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
}
