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

package com.huawei.common.constant;

/**
 * 返回值
 *
 * @since 2021-10-30
 */
public class ResultCode {
    /**
     * 失败
     */
    public static final int FAIL = -1;

    /**
     * 服务器信息为空
     */
    public static final int SERVER_INFO_NULL = -2;

    /**
     * 脚本名已存在
     */
    public static final int SCRIPT_NAME_EXISTS = -3;

    /**
     * 参数异常
     */
    public static final int PARAM_INVALID = -4;

    private ResultCode() {
    }
}
