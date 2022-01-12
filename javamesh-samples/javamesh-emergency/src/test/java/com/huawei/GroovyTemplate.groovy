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
import net.grinder.scriptengine.groovy.junit.annotation.AfterProcess;
import net.grinder.scriptengine.groovy.junit.annotation.AfterThread;
import org.hamcrest.Matchers;
import org.junit.After;

import static net.grinder.script.Grinder.grinder;
import static org.junit.Assert.*;
import net.grinder.plugin.http.HTTPPluginControl;
import net.grinder.plugin.http.HTTPRequest;
import net.grinder.script.GTest;
import net.grinder.scriptengine.groovy.junit.GrinderRunner;
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess;
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread;
import net.grinder.scriptengine.groovy.junit.GrinderRunner

import org.junit.runner.RunWith
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import HTTPClient.CookieModule;
import HTTPClient.HTTPResponse;
import HTTPClient.NVPair;

@RunWith(GrinderRunner)
class GroovyTemplate {
    public static int testNumber = 0;
    public static HTTPRequest request;
    public Map variables;
    public static GTest 事务控制器1;
    public static GTest 事务控制器2;
    public static GTest 事务控制器3;

    @BeforeProcess
    public static void beforeProcess() {
        HTTPPluginControl.getConnectionDefaults().timeout = 6000;
        request = new HTTPRequest();
        事务控制器1 = new GTest(nextTestNumber(), "事务控制器1")
        事务控制器1.record(this, "事务控制器1")
        事务控制器2 = new GTest(nextTestNumber(), "事务控制器2")
        事务控制器2.record(this, "事务控制器2")
        事务控制器3 = new GTest(nextTestNumber(), "事务控制器3")
        事务控制器3.record(this, "事务控制器3")
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
        CookieModule.addCookie(new Cookie(""),threadContext);
    }

    @Test
    public void test() {
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

    @Test
    public void "事务控制器1"() {
        int vusers = getVusers();
        int runThreadNum = getRunThreadNum();
        int preRunNum = vusers / 100 * 0;int runNum = vusers / 100 * (0 + 20);
        if (runThreadNum <= preRunNum || runThreadNum > runNum)
            return;
    }

    @Test
    public void "事务控制器2"() {
        int vusers = getVusers();
        int runThreadNum = getRunThreadNum();
        int preRunNum = vusers / 100 * 20;int runNum = vusers / 100 * (20 + 30);
        if (runThreadNum <= preRunNum || runThreadNum > runNum)
            return;
    }

    @Test
    public void "事务控制器3"() {
        int vusers = getVusers();
        int runThreadNum = getRunThreadNum();
        int preRunNum = vusers / 100 * 50;int runNum = vusers / 100 * (50 + 50);
        if (runThreadNum <= preRunNum || runThreadNum > runNum)
            return;
        def request0 = new HTTPRequest();
        httpResult = request0.GET("http://127.0.0.1:9093/argus-emergency/api/plan",[] as NVPair[])
    }

    public static int nextTestNumber() {
        return ++testNumber;
    }

    public int getVusers() {
        int totalAgents = Integer.parseInt(grinder.getProperties().get("grinder.agents").toString())
        int totalProcesses = Integer.parseInt(grinder.properties.get("grinder.processes").toString())
        int totalThreads = Integer.parseInt(grinder.properties.get("grinder.threads").toString())
        return totalAgents * totalProcesses * totalThreads
    }

    public int getRunThreadNum() {
        int agentNum = grinder.agentNumber
        int processNum = grinder.processNumber
        int threadNum = grinder.threadNumber
        return  (agentNum + 1) * (processNum + 1) * (threadNum + 1)
    }
}