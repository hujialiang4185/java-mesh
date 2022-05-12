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
 * 错误信息
 *
 * @since 2021-10-30
 */
public class FailedInfo {
    /**
     * 删除失败
     */
    public static final String DELETE_FAILED = "删除失败";
    /**
     * 删除未全部成功
     */
    public static final String DELETE_NOT_SUCCESS_ALL = "删除未全部成功";
    /**
     * 服务器信息为空
     */
    public static final String SERVER_INFO_NULL = "执行状态为远程执行时，服务器信息不能为空";
    /**
     * 删除脚本失败
     */
    public static final String DELETE_SCRIPT_FROM_SCENE_FAIL = "从场景中删除脚本失败";
    /**
     * 修改场景失败
     */
    public static final String UPDATE_SCENE_FAIL = "修改场景失败";
    /**
     * 上传文件失败
     */
    public static final String UPLOAD_FAIL = "文件上传失败";
    /**
     * 脚本信息不存在
     */
    public static final String SCRIPT_NOT_EXISTS = "脚本信息不存在";
    /**
     * 新建脚本失败
     */
    public static final String SCRIPT_CREATE_FAIL = "新建脚本失败";
    /**
     * 参数异常
     */
    public static final String PARAM_INVALID = "参数异常";
    /**
     * 脚本名已存在
     */
    public static final String SCRIPT_NAME_EXISTS = "脚本名已存在";
    /**
     * 提审失败
     */
    public static final String SUBMIT_REVIEW_FAIL = "提审失败";
    /**
     * 下载文件失败
     */
    public static final String DOWNLOAD_SCRIPT_FAIL = "下载文件失败";
    /**
     * 权限不足
     */
    public static final String INSUFFICIENT_PERMISSIONS = "权限不足";
    /**
     * 审核失败
     */
    public static final String APPROVE_FAIL = "审核失败";
    /**
     * 用户没有分组,请先对用户分组后操作
     */
    public static final String USER_HAVE_NOT_GROUP = "用户没有分组,请先对用户分组后操作";
    /**
     * 文件修改失败
     */
    public static final String UPDATE_SCRIPT_FAIL = "文件修改失败";

    private FailedInfo() {
    }
}
