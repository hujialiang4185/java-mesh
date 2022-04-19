package com.huawei.agent.service.impl;

import com.huawei.agent.common.api.CommonResult;
import com.huawei.agent.entity.ExecParam;
import com.huawei.agent.entity.ExecResult;
import com.huawei.agent.service.EmergencyAgentService;
import com.huawei.agent.util.RemoteServerCacheUtil;
import com.huawei.agent.util.RestTemplateUtil;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.script.*;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class EmergencyAgentServiceImpl implements EmergencyAgentService {

    @Value("${remoteServer.execComplete}")
    private String execCompleteUrl;

    @Resource(name = "scriptExecThreadPool")
    private ThreadPoolExecutor executor;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put("python.console.encoding", "UTF-8");
        PythonInterpreter.initialize(System.getProperties(), props, new String[0]);
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
        executor.execute(new ExecutorHandler(content, param, detailId, scriptType, execParam.getScriptName()));
        return CommonResult.success();
    }

    @Override
    public CommonResult cancel(int detailId, String scriptType) {
        ExecutorHandler executorHandler = new ExecutorHandler();
        if (scriptType.equals("0")) {
            executorHandler.cancelShell(detailId);
        } else {
            executorHandler.cancelGroovyAndPython(detailId);
        }
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
        private String content;

        private String param;

        private int detailId;

        private String scriptType;

        private String scriptName;

        private static final String TYPE_SHELL = "0";

        private static final String TYPE_PYTHON = "1";

        private static final String TYPE_GROOVY = "2";

        private String scriptLocation = "/tmp/";

        private static final String SH = "/bin/sh";

        //private static final String SH = "cmd";

        private static final String SH_C = "-C";

        //private static final String SH_C = "/C";

        public ExecutorHandler(String content, String param, int detailId, String scriptType, String scriptName) {

            this.content = content;
            this.param = param;
            this.detailId = detailId;
            this.scriptType = scriptType;
            this.scriptName = scriptName;
        }

        public ExecutorHandler() {
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
                }
            } catch (Exception e) {
                log.error("Exception occurs. Exception info", e);
                onComplete(ExecResult.fail(detailId, "Exec script fail. "));
            }
        }

        private void execGroovy() {
            cache.put(detailId, String.valueOf(Thread.currentThread().getId()));
            ScriptEngineManager factory = new ScriptEngineManager();

            //每次生成一个engine实例
            ScriptEngine engine = factory.getEngineByName("groovy");
            if (engine == null) {
                throw new IllegalArgumentException("不存在groovy执行引擎");
            }
            if (StringUtils.isNotBlank(param)) {
                String[] params = param.split(",");

                // 初始化Bindings
                Bindings bindings = engine.createBindings();
                for (String keyValue : params) {
                    String[] split = keyValue.split("=");
                    bindings.put(split[0], split[1]);
                }
            }
            StringWriter sw = new StringWriter();
            StringWriter swError = new StringWriter();
            try {
                ScriptContext context = engine.getContext();
                context.setWriter(sw);
                context.setErrorWriter(swError);
                engine.eval(content);
                String errorInfo = swError.toString();
                if (StringUtils.isNotBlank(errorInfo)) {
                    onComplete(ExecResult.fail(detailId, errorInfo));
                } else {
                    onComplete(ExecResult.success(detailId, sw.toString()));
                }
            } catch (ScriptException e) {
                log.error("exec groovy script {} failed.", detailId, e);
                onComplete(ExecResult.fail(detailId,
                    sw.toString() + e.getMessage()));
            } finally {
                try {
                    sw.close();
                } catch (Exception e) {
                    log.error("close exec groovy script normalOutputStream error.",e);
                }
                try {
                    swError.close();
                } catch (Exception e) {
                    log.error("close exec groovy script errorOutputStream error.",e);
                }
                cache.remove(detailId);
            }
        }

        private void execPython() throws IOException {
            cache.put(detailId, String.valueOf(Thread.currentThread().getId()));
            PythonInterpreter interpreter = new PythonInterpreter();
            if (StringUtils.isNotBlank(param)) {
                String[] params = param.split(",");
                for (String keyValue : params) {
                    String[] split = keyValue.split("=");
                    interpreter.set(split[0], split[1]);
                }
            }
            String pythonFile = "";
            ByteArrayOutputStream normalOutputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
            try {
                interpreter.setOut(normalOutputStream);
                interpreter.setErr(errorOutputStream);
                pythonFile = createPythonFile(scriptName, content);
                interpreter.execfile(pythonFile);
                String errorInfo = errorOutputStream.toString("UTF-8");
                if (StringUtils.isNotBlank(errorInfo)) {
                    onComplete(ExecResult.fail(detailId, errorInfo));
                } else {
                    onComplete(ExecResult.success(detailId, normalOutputStream.toString("UTF-8")));
                }
            } catch (PyException e) {
                log.error("exec python script {} failed.", detailId, e);
                onComplete(ExecResult.fail(detailId,
                    normalOutputStream.toString("UTF-8") + e));
            } finally {
                if (StringUtils.isNotEmpty(pythonFile)) {
                    File file = new File(pythonFile);
                    if (file.exists() && file.delete()) {
                        log.info("script file {} was deleted.", pythonFile);
                    }
                }
                cache.remove(detailId);
                try {
                    normalOutputStream.close();
                } catch (Exception e) {
                    log.error("close exec python script normalOutputStream error.",e);
                }
                try {
                    errorOutputStream.close();
                } catch (Exception e) {
                    log.error("close exec python script errorOutputStream error.",e);
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
                log.info("python file {} was created.", fileName);
            }
            return fileName;
        }

        private void execShell() {
            String fileName = "";
            try {
                fileName = createScriptFile(scriptName, content);
                if (param != null) {
                    onComplete(execute(commands(fileName, param.split(","))));
                } else {
                    onComplete(execute(commands(fileName, "".split(","))));
                }
            } catch (FileNotFoundException e) {
                onComplete(ExecResult.fail(detailId, "Please check out your scriptLocation."));
            } catch (IOException e) {
                log.error("Failed to create local script.", e);
                onComplete(ExecResult.fail(detailId, e.getMessage()));
            } finally {
                if (StringUtils.isNotEmpty(fileName)) {
                    File file = new File(fileName);
                    if (file.exists() && file.delete()) {
                        log.info("script file {} was deleted.", fileName);
                    }
                }
                cache.remove(detailId);
            }
        }

        private ExecResult execute(String[] commands) {
            try {
                Process exec = Runtime.getRuntime().exec(commands);
                String info = parseResult(exec.getInputStream());
                String errorInfo = parseResult(exec.getErrorStream());
                if (exec.waitFor() == 0) {
                    if (exec.exitValue() == 0) {
                        return ExecResult.success(detailId, info);
                    } else {
                        return ExecResult.fail(detailId, errorInfo);
                    }
                } else {
                    return ExecResult.fail(detailId, errorInfo);
                }
            } catch (IOException | InterruptedException e) {
                return ExecResult.fail(detailId, e.getMessage());
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
                log.info("script file {} was created.", fileName);
            }
            return fileName;
        }

        private String[] commands(String command, String[] params) {
            return (String[]) ArrayUtils.addAll(new String[]{SH, SH_C, command}, params);
        }

        private ExecResult cancelShell(int detailId) {
            String pid = cache.get(detailId);
            return execute(commands(String.format(Locale.ROOT, "kill -9 %s", pid), null));
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

    /* public static void main(String[] args) throws Exception {

     *//*Thread t = new Thread(new InnerRunnable());
        t.start();
        System.out.println("线程id:"+t.getId());
        System.out.println("       thread: " + t);
        long threadId = t.getId();

        Thread s = findThread(threadId);
        System.out.println("  find thread: " + s);
        System.out.println("current thread: " + Thread.currentThread());
        s.interrupt();*//*

     *//*PythonInterpreter interpreter = new PythonInterpreter();
        StringWriter sw = new StringWriter();
        interpreter.setOut(sw);
        interpreter.set("a", "hello aaa");
        interpreter.set("b", 1);
        interpreter.set("c", 2);
        interpreter.execfile("D:\\Runtime.py");
        System.out.println("sw:" + sw);*//*



     *//*Process proc;
        try {
            proc = Runtime.getRuntime().exec("cmd /c python D:\\Runtime.py");// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            String line = null;
            String errorLine = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            while ((errorLine = error.readLine())!=null){
                System.out.println(errorLine);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*//*


     *//*ScriptEngineManager factory = new ScriptEngineManager();
        //每次生成一个engine实例
        ScriptEngine engine = factory.getEngineByName("groovy");

        // 初始化Bindings
        Bindings bindings = engine.createBindings();
        // 绑定参数
        bindings.put("date", new Date());
        bindings.put("name", "groovy");
        final String name = "groovy";
        // 定义groovy脚本中执行方法的名称
        final String scriptName = "execute";
        // 定义groovy脚本内容
        final String scriptContent = "def " + scriptName + "(){" +
                "    println(\"now dateTime is: ${date.getTime()}\");" +
                "    println(\"my name is $name\");" +
                "    return date.getTime() > 0;" +
                "}" +
                "\r\n return execute()";
        System.out.println(scriptContent);

//
            // 执行脚本
            StringWriter sw = new StringWriter();

            engine.getContext().setWriter(sw);

            Object eval = engine.eval(scriptContent, bindings);

            System.out.println("sw = " + sw);


            System.out.println("eval" + eval);

            System.out.println("---------------------------------------");*//*
    }*/
}


