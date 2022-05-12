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

import lombok.Data;

import java.util.List;

/**
 * 参数模板格式
 *
 * @param <ObjectType> 需要包装的数据类型
 * @author y30010171
 * @since 2021-11-09
 **/
@Data
public class CommonPage<ObjectType> {
    private static final int DEFAULT_SIZE = 10;
    /**
     * 接收的对象
     */
    private ObjectType object;

    /**
     * 接收对象的集合
     */
    private List<ObjectType> objectList;

    /**
     * 页码
     */
    private int pageIndex = 1;

    /**
     * 分页大小
     */
    private int pageSize = DEFAULT_SIZE;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序类型 asc正序 desc倒叙
     */
    private String sortType;
}