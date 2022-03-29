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

package com.huawei.common.config;

import com.huawei.argus.listener.ITestLifeCycleListener;
import com.huawei.common.constant.ValidEnum;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyExecRecordDetail;
import com.huawei.emergency.entity.EmergencyExecRecordDetailExample;
import com.huawei.emergency.mapper.EmergencyExecRecordDetailMapper;
import com.huawei.emergency.mapper.EmergencyExecRecordMapper;
import com.huawei.emergency.service.impl.ExecRecordHandlerFactory;
import com.huawei.script.exec.ExecResult;

import org.ngrinder.model.PerfTest;
import org.ngrinder.model.StatusCategory;
import org.ngrinder.perftest.service.PerfTestRunnable;
import org.ngrinder.service.IPerfTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 压测任务完成监听器
 *
 * @author y30010171
 * @since 2022-02-17
 **/
@Component("perfTestListener")
public class PerfTestListenerConfig implements ITestLifeCycleListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerfTestListenerConfig.class);

    @Autowired
    private ExecRecordHandlerFactory handlerFactory;

    @Autowired
    private EmergencyExecRecordMapper recordMapper;

    @Autowired
    private EmergencyExecRecordDetailMapper recordDetailMapper;

    @Resource(name = "scriptExecThreadPool")
    private ThreadPoolExecutor poolExecutor;

    @PostConstruct
    public void registryListener() {
        PerfTestRunnable.allTestLifeCycleListeners.add(this);
        LOGGER.info("PerfTestListener was register.");
    }

    @Override
    public void start(PerfTest perfTest, IPerfTestService iPerfTestService, String version) {
        LOGGER.info("perf_test {} is starting. {}", perfTest.getId(), version);
    }

    @Override
    public void finish(PerfTest perfTest, String stopReason, IPerfTestService iPerfTestService, String version) {
        LOGGER.info("perf_test {} is end. {} . {}", perfTest.getId(), stopReason, version);
        try {
            EmergencyExecRecordDetailExample detailExample = new EmergencyExecRecordDetailExample();
            detailExample.createCriteria()
                .andPerfTestIdEqualTo(perfTest.getId().intValue())
                .andIsValidEqualTo(ValidEnum.VALID.getValue());
            List<EmergencyExecRecordDetail> details =
                recordDetailMapper.selectByExample(detailExample);
            if (details.size() == 0) {
                LOGGER.warn("can't found record_detail by perf_test {}", perfTest.getId());
                return;
            }
            ExecResult result;
            StatusCategory category = perfTest.getStatus().getCategory();
            if (category == StatusCategory.ERROR || category == StatusCategory.STOP) {
                result = ExecResult.fail(perfTest.getProgressMessage() + perfTest.getLastProgressMessage());
            } else {
                result = ExecResult.success(perfTest.getProgressMessage() + perfTest.getLastProgressMessage());
            }
            EmergencyExecRecord record = recordMapper.selectByPrimaryKey(details.get(0).getRecordId());
            poolExecutor.execute(() -> handlerFactory.completePerfTest(record, details.get(0), result));
        } catch (Exception e) {
            LOGGER.info("perf_test {} finished but callback error.{}.{}", perfTest.getId(), stopReason,
                e.getMessage());
        }
    }
}