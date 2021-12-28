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


import HTTPClient.Cookie
import HTTPClient.HTTPResponse
import com.huawei.test.asserts.RegularAssert
import com.huawei.test.postprocessor.config.RegularExtractorConfig
import net.grinder.scriptengine.groovy.junit.annotation.AfterProcess;
import net.grinder.scriptengine.groovy.junit.annotation.AfterThread;
import org.junit.After
import org.junit.Assert;

import static net.grinder.script.Grinder.grinder;
import net.grinder.plugin.http.HTTPPluginControl;
import net.grinder.plugin.http.HTTPRequest;
import net.grinder.script.GTest;
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess;
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread;
import net.grinder.scriptengine.groovy.junit.GrinderRunner

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import HTTPClient.CookieModule;

@RunWith(GrinderRunner)
class Test1 {
    public static int testNumber = 0;
    public static HTTPRequest request;
    public static GTest TransactionController;

    @BeforeProcess
    public static void beforeProcess() {
        HTTPPluginControl.getConnectionDefaults().timeout = 6000;
        request = new HTTPRequest();
        TransactionController = new GTest(nextTestNumber(), "TransactionController")
        TransactionController.record(this, "TransactionController")
    }

    @BeforeThread
    public void beforeThread() {
        grinder.statistics.delayReports = true;
        // reset to the all cookies
        def threadContext = HTTPPluginControl.getThreadHTTPClientContext();
        CookieModule.listAllCookies(threadContext).each {
            CookieModule.removeCookie(it, threadContext);
        }
    }

    @Before
    public void before() {
        def threadContext = HTTPPluginControl.getThreadHTTPClientContext();
    }

    @Test
    public void test() {
        int vusers = getVusers();
        int runThreadNum = getRunThreadNum();
        int preRate = 0;
        preRate += 100; int runNum0 = vusers / 100 * preRate;
        if (runThreadNum > 0 && runThreadNum <= runNum0)
            this."TransactionController"();
    }

    @After
    public void after() {
    }

    @AfterThread
    public void afterThread() {
    }

    @AfterProcess
    public static void afterProcess() {
    }

    public void "TransactionController"() {

    }

    public static int nextTestNumber() {
        return ++testNumber;
    }

    public int getVusers() {
        Assert.assertNotNull("grinder运行上下文不存在", grinder)
        Assert.assertNotNull("grinder.properties 不存在",grinder.properties)
        int totalAgents = Integer.parseInt(grinder.getProperties().get("grinder.agents","0").toString())
        int totalProcesses = Integer.parseInt(grinder.properties.get("grinder.processes","0").toString())
        int totalThreads = Integer.parseInt(grinder.properties.get("grinder.threads","0").toString())
        return totalAgents * totalProcesses * totalThreads
    }

    public int getRunThreadNum() {
        int agentNum = grinder.agentNumber
        int processNum = grinder.processNumber
        int threadNum = grinder.threadNumber
        return (agentNum + 1) * (processNum + 1) * (threadNum + 1)
    }
}