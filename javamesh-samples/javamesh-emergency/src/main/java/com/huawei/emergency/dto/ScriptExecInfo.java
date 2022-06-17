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

package com.huawei.emergency.dto;

import com.huawei.common.util.ServerInfo;

import lombok.Data;

/**
 * 待执行的脚本信息
 *
 * @author y30010171
 * @since 2021-10-20
 **/
@Data
public class ScriptExecInfo {
    /**
     * 任务详情ID
     */
    private int detailId;
    /**
     * 脚本名称
     */
    private String scriptName;
    /**
     * 脚本内容
     */
    private String scriptContent;
    /**
     * 脚本类型
     */
    private String scriptType;
    /**
     * 脚本的存放路径
     */
    private String scriptLocation;
    /**
     * <p>运行此脚本所需要的远程服务器信息</p>
     * <p>如果为本地执行，则无需理会此字段 </p>
     */
    private ServerInfo remoteServerInfo;

    /**
     * 运行参数
     */
    private String[] params;

    /**
     * 超时时间
     */
    private long timeOut;

    private Integer perfSceneId;
    private String perfSceneName;
    private Integer perfTestId;
    private String perfTestName;

    private int recordId;
    private String content;
    private String param;
}
