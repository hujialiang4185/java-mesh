/*
 * Copyright (C) Ltd. 2021-2021. Huawei Technologies Co., All rights reserved
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

package com.huawei.agent.entity;

import lombok.Data;

/**
 * agent 执行结果
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Data
public class ExecResult {
    private int detailId;
    private String msg;
    private int code;

    public static ExecResult success(int recordId, String info) {
        ExecResult execResult = new ExecResult();
        execResult.setDetailId(recordId);
        execResult.setMsg(info);
        execResult.setCode(0);
        return execResult;
    }

    public static ExecResult fail(int recordId, String errorInfo) {
        ExecResult execResult = new ExecResult();
        execResult.setDetailId(recordId);
        execResult.setMsg(errorInfo);
        execResult.setCode(-1);
        return execResult;
    }
}
