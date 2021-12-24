/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved
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

import com.huawei.EmergencyDrillApplication
import com.huawei.emergency.entity.EmergencyExecRecordDetail
import com.huawei.emergency.entity.EmergencyScript
import com.huawei.emergency.mapper.EmergencyExecMapper
import com.huawei.emergency.mapper.EmergencyExecRecordMapper
import com.huawei.emergency.service.impl.EmergencyExecServiceImpl
import com.huawei.emergency.service.impl.ExecRecordHandlerFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.ThreadPoolExecutor

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmergencyDrillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmergencyExecServiceTest extends GroovyTestCase {

    @Autowired
    EmergencyExecServiceImpl execService;

    @Mock
    EmergencyExecMapper execMapper;

    @Mock
    EmergencyExecRecordMapper recordMapper;

    @Mock
    ThreadPoolExecutor threadPoolExecutor;

    @Mock
    ExecRecordHandlerFactory handlerFactory;

    def test_script = new EmergencyScript(
            scriptId: 0,
            scriptName: 'test',
            content: 'echo "Hello, World!"',
            scriptType: '0',
            submitInfo: '测试')

    @Before
    void "mock"() {
        execService.setExecMapper(execMapper)
        execService.setRecordMapper(recordMapper)
        execService.setThreadPoolExecutor(threadPoolExecutor)
        execService.setHandlerFactory(handlerFactory)
    }

    @Test
    void "exec script when script is null"() {
        assertNotNull(execService.exec(null).getMsg())
        assertNotNull(execService.exec(new EmergencyScript()).getMsg())
    }

    @Test
    void "exec script when script is debugging"() {
        Mockito.when(recordMapper.countByExample(ArgumentMatchers.any())).thenReturn(1L)
        assertNotNull(execService.exec(null).getMsg())
    }

    @Test
    void "exec script when script is normal"() {
        Mockito.when(recordMapper.countByExample(ArgumentMatchers.any())).thenReturn(0L) // no debugging
        Mockito.when(handlerFactory.generateRecordDetail(ArgumentMatchers.any())).thenReturn([new EmergencyExecRecordDetail(detailId: 1)])
        def exec = execService.exec(test_script)
        assertNull(exec.msg)
        def data = exec.data
        assertNotNull(data)
        assertEquals(1, data.properties.get("debugId"))
    }


    void "ensure when not failed"(){
    }
}