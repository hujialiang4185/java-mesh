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

package com.huawei.agent.service.impl;

import com.huawei.agent.common.api.CommonResult;
import com.huawei.agent.entity.ExecParam;
import com.huawei.agent.entity.ExecResult;
import com.huawei.agent.service.EmergencyAgentService;
import com.huawei.agent.util.RemoteServerCacheUtil;
import com.huawei.agent.util.RestTemplateUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

/**
 * 执行agent
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Service
public class EmergencyAgentServiceImpl implements EmergencyAgentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmergencyAgentServiceImpl.class);
    private static final String UTF_8 = "UTF-8";
    private static final String SPLIT_SIGN = ",";
    /**
     * 脚本执行ID 与 线程的pid的映射关系
     */
    private static Map<Integer, String> cache = new HashMap<>();
    /**
     * 脚本执行ID 与 请求token的映射关系
     */
    private static Map<Integer, String> requestCache = new HashMap<>();

    @Value("${remoteServer.execComplete}")
    private String execCompleteUrl;

    @Resource(name = "scriptHandlerThreadPool")
    private ThreadPoolExecutor scriptHandlerExecutor;

    @Resource(name = "scriptExecThreadPool")
    private ThreadPoolExecutor scriptExecExecutor;

    @Value("${script.executor.timeOut}")
    private long timeOutSecond;

    private ScriptEngine groovyEngine;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put("python.console.encoding", UTF_8);
        PythonInterpreter.initialize(System.getProperties(), props, new String[0]);
        groovyEngine = new ScriptEngineManager().getEngineByName("groovy");
        if (groovyEngine == null) {
            throw new RuntimeException("不存在groovy执行引擎");
        }
    }

    @Override
    public CommonResult exec(HttpServletRequest request, ExecParam execParam) {
        String content = execParam.getContent();
        String param = execParam.getParam();
        int detailId = execParam.getDetailId();
        String scriptType = execParam.getScriptType();
        if (StringUtils.isBlank(content) || StringUtils.isBlank(scriptType) || detailId == 0) {
            return CommonResult.failed("The param required is null. ");
        }
        requestCache.put(detailId, request.getHeader("Cookie"));
        scriptHandlerExecutor.execute(
            new ExecutorHandler(content, param, detailId, scriptType, execParam.getScriptName()));
        return CommonResult.success();
    }

    @Override
    public CommonResult cancel(int detailId, String scriptType) {
        ExecutorHandler executorHandler = new ExecutorHandler();
        if ("0".equals(scriptType)) {
            executorHandler.cancelShell(detailId);
        } else {
            executorHandler.cancelGroovyAndPython(detailId);
        }
        onComplete(ExecResult.fail(detailId, "执行取消"));
        return CommonResult.success();
    }

    @Override
    public void onComplete(ExecResult execResult) {
        int detailId = execResult.getDetailId();
        String cookie = requestCache.get(detailId);
        String url = String.format(Locale.ROOT, "%s%s", RemoteServerCacheUtil.getServerInfo(), execCompleteUrl);
        RestTemplateUtil.sendPostRequest(cookie, url, execResult);
        requestCache.remove(detailId);
    }

    class ExecutorHandler implements Runnable {
        private static final String TYPE_SHELL = "0";
        private static final String TYPE_PYTHON = "1";
        private static final String TYPE_GROOVY = "2";
        private static final String SH = "/bin/sh";
        private static final String SH_C = "-C";
        private String content;
        private String param;
        private int detailId;
        private String scriptType;
        private String scriptName;
        private String scriptLocation = "/tmp/";

        ExecutorHandler(String content, String param, int detailId, String scriptType, String scriptName) {
            this.content = content;
            this.param = param;
            this.detailId = detailId;
            this.scriptType = scriptType;
            this.scriptName = scriptName;
        }

        ExecutorHandler() {
        }

        @Override
        public void run() {
            try {
                switch (scriptType) {
                    case TYPE_SHELL:
                        execShell();
                        break;
                    case TYPE_PYTHON:
                        execPython();
                        break;
                    case TYPE_GROOVY:
                        execGroovy();
                        break;
                    default:
                        LOGGER.error("invalid script type {}", scriptType);
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("Exception occurs. Exception info", e);
                onComplete(ExecResult.fail(detailId, "Exec script fail. "));
            }
        }

        private void execGroovy() {
            cache.put(detailId, String.valueOf(Thread.currentThread().getId()));
            if (StringUtils.isNotBlank(param)) {
                String[] params = param.split(SPLIT_SIGN);
                Bindings bindings = groovyEngine.createBindings();
                for (String keyValue : params) {
                    String[] split = keyValue.split("=");
                    bindings.put(split[0], split[1]);
                }
            }
            Future<ExecResult> scriptExecTask = null;
            try {
                scriptExecTask = scriptExecExecutor.submit(() -> {
                    StringWriter sw = new StringWriter();
                    StringWriter swError = new StringWriter();
                    ScriptContext context = groovyEngine.getContext();
                    context.setWriter(sw);
                    context.setErrorWriter(swError);
                    try {
                        groovyEngine.eval(content);
                        String errorInfo = swError.toString();
                        return StringUtils.isNotBlank(errorInfo) ? ExecResult.fail(detailId, errorInfo)
                            : ExecResult.success(detailId, sw.toString());
                    } catch (ScriptException e) {
                        LOGGER.error("exec groovy script {} failed.", detailId, e);
                        return ExecResult.fail(detailId, sw + e.getMessage());
                    } finally {
                        try {
                            sw.close();
                        } catch (IOException e) {
                            LOGGER.error("close exec groovy script normalOutputStream error.", e);
                        }
                        try {
                            swError.close();
                        } catch (IOException e) {
                            LOGGER.error("close exec groovy script errorOutputStream error.", e);
                        }
                    }
                });
                if (timeOutSecond > 0) {
                    onComplete(scriptExecTask.get(timeOutSecond, TimeUnit.SECONDS));
                } else {
                    onComplete(scriptExecTask.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("exec groovy script {} failed.", detailId, e);
                onComplete(ExecResult.fail(detailId, e.getMessage()));
            } catch (TimeoutException e) {
                LOGGER.error("exec groovy time out.", e);
                onComplete(ExecResult.fail(detailId, "timeOut"));
            } finally {
                cache.remove(detailId);
                if (scriptExecTask != null) {
                    scriptExecTask.cancel(true);
                }
            }
        }

        private void execPython() throws IOException {
            cache.put(detailId, String.valueOf(Thread.currentThread().getId()));
            PythonInterpreter interpreter = new PythonInterpreter();
            if (StringUtils.isNotBlank(param)) {
                String[] params = param.split(SPLIT_SIGN);
                for (String keyValue : params) {
                    String[] split = keyValue.split("=");
                    interpreter.set(split[0], split[1]);
                }
            }
            String pythonFile = "";
            Future<ExecResult> scriptExecTask = null;
            try {
                pythonFile = createPythonFile(scriptName, content);
                String finalPythonFile = pythonFile;
                scriptExecTask = scriptExecExecutor.submit(() -> {
                    ByteArrayOutputStream normalOutputStream = new ByteArrayOutputStream();
                    ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
                    interpreter.setOut(normalOutputStream);
                    interpreter.setErr(errorOutputStream);
                    try {
                        interpreter.execfile(finalPythonFile);
                        String errorInfo = errorOutputStream.toString(UTF_8);
                        return StringUtils.isNotBlank(errorInfo) ? ExecResult.fail(detailId, errorInfo)
                            : ExecResult.success(detailId, normalOutputStream.toString(UTF_8));
                    } catch (PyException e) {
                        LOGGER.error("exec python script {} failed.", detailId, e);
                        return ExecResult.fail(detailId,
                            normalOutputStream.toString(UTF_8) + e);
                    } finally {
                        try {
                            normalOutputStream.close();
                        } catch (IOException e) {
                            LOGGER.error("close exec python script normalOutputStream error.", e);
                        }
                        try {
                            errorOutputStream.close();
                        } catch (IOException e) {
                            LOGGER.error("close exec python script errorOutputStream error.", e);
                        }
                    }
                });
                if (timeOutSecond > 0) {
                    onComplete(scriptExecTask.get(timeOutSecond, TimeUnit.SECONDS));
                } else {
                    onComplete(scriptExecTask.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("exec python script {} failed.", detailId, e);
                onComplete(ExecResult.fail(detailId, e.getMessage()));
            } catch (TimeoutException e) {
                LOGGER.error("exec python time out.", e);
                onComplete(ExecResult.fail(detailId, "timeOut"));
            } finally {
                if (StringUtils.isNotEmpty(pythonFile)) {
                    File file = new File(pythonFile);
                    if (file.exists() && file.delete()) {
                        LOGGER.info("script file {} was deleted.", pythonFile);
                    }
                }
                cache.remove(detailId);
                if (scriptExecTask != null) {
                    scriptExecTask.cancel(true);
                }
            }
        }

        private String createPythonFile(String scriptName, String scriptContent) throws IOException {
            String fileName = String.format(Locale.ROOT, "%s%s-%s.py",
                scriptLocation, scriptName, System.nanoTime());
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                fileOutputStream.write("#-*- coding:utf-8 –*-".getBytes(StandardCharsets.UTF_8));
                fileOutputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                fileOutputStream.write(scriptContent.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
                LOGGER.info("python file {} was created.", fileName);
            }
            return fileName;
        }

        private void execShell() {
            String fileName = "";
            try {
                fileName = createScriptFile(scriptName, content);
                String[] scriptParams = new String[0];
                if (param != null) {
                    scriptParams = param.split(SPLIT_SIGN);
                }
                onComplete(execute(commands(fileName, scriptParams), timeOutSecond));
            } catch (FileNotFoundException e) {
                onComplete(ExecResult.fail(detailId, "Please check out your scriptLocation."));
            } catch (IOException e) {
                LOGGER.error("Failed to create local script.", e);
                onComplete(ExecResult.fail(detailId, e.getMessage()));
            } finally {
                if (StringUtils.isNotEmpty(fileName)) {
                    File file = new File(fileName);
                    if (file.exists() && file.delete()) {
                        LOGGER.info("script file {} was deleted.", fileName);
                    }
                }
                cache.remove(detailId);
            }
        }

        private ExecResult execute(String[] commands, long timeOut) {
            Process process = null;
            Future<String> scriptLogTask = null;
            Future<String> scriptErrorLogTask = null;
            try {
                process = Runtime.getRuntime().exec(commands);
                Process finalProcess = process;
                scriptLogTask = scriptExecExecutor.submit(() -> parseResult(finalProcess.getInputStream()));
                scriptErrorLogTask = scriptExecExecutor.submit(() -> parseResult(finalProcess.getErrorStream()));
                String scriptLog;
                String scriptErrorLog;
                if (timeOut > 0) {
                    scriptLog = scriptLogTask.get(timeOut, TimeUnit.SECONDS);
                    scriptErrorLog = scriptErrorLogTask.get(timeOut, TimeUnit.SECONDS);
                    if (process.waitFor(timeOut, TimeUnit.SECONDS)) {
                        return process.exitValue() == 0 ? ExecResult.success(detailId, scriptLog)
                            : ExecResult.fail(detailId, scriptErrorLog);
                    }
                } else {
                    scriptLog = scriptLogTask.get();
                    scriptErrorLog = scriptErrorLogTask.get();
                    if (process.waitFor() == 0) {
                        return process.exitValue() == 0 ? ExecResult.success(detailId, scriptLog)
                            : ExecResult.fail(detailId, scriptErrorLog);
                    }
                }
                return ExecResult.fail(detailId, scriptErrorLog);
            } catch (IOException | InterruptedException | ExecutionException e) {
                LOGGER.error("exec error.", e);
                return ExecResult.fail(detailId, e.getMessage());
            } catch (TimeoutException e) {
                LOGGER.error("exec shell time out.", e);
                return ExecResult.fail(detailId, "timeOut");
            } finally {
                if (scriptLogTask != null) {
                    scriptLogTask.cancel(true);
                }
                if (scriptErrorLogTask != null) {
                    scriptErrorLogTask.cancel(true);
                }
                if (process != null) {
                    process.destroy();
                }
            }
        }

        private String parseResult(InputStream inputStream) throws IOException {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String lines;
            boolean readFirstLogAsPid = true;
            while ((lines = bufferedReader.readLine()) != null) {
                if (readFirstLogAsPid) {
                    cache.put(detailId, lines);
                    readFirstLogAsPid = false;
                } else {
                    result.append(lines).append(System.lineSeparator());
                }
            }
            return result.toString();
        }

        private String createScriptFile(String scriptName, String scriptContent) throws IOException {
            String fileName = String.format(Locale.ROOT, "%s%s-%s.sh",
                scriptLocation, scriptName, System.nanoTime());
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                fileOutputStream.write(("echo $$" + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                fileOutputStream.write(scriptContent.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
                LOGGER.info("script file {} was created.", fileName);
            }
            return fileName;
        }

        private String[] commands(String command, String[] params) {
            return (String[]) ArrayUtils.addAll(new String[]{SH, SH_C, command}, params);
        }

        private ExecResult cancelShell(int detailId) {
            String pid = cache.get(detailId);
            return execute(commands(String.format(Locale.ROOT, "kill -9 %s", pid), null), 0);
        }

        private void cancelGroovyAndPython(int detailId) {
            long tid = Long.parseLong(cache.get(detailId));
            Thread thread = findThread(tid);
            if (thread != null) {
                thread.interrupt();
            }
        }
    }

    private Thread findThread(long threadId) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while (group != null) {
            Thread[] threads = new Thread[(int) (group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            for (int i = 0; i < count; i++) {
                if (threadId == threads[i].getId()) {
                    return threads[i];
                }
            }
            group = group.getParent();
        }
        return null;
    }
}