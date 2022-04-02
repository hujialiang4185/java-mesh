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

package com.huawei.script.exec.executor;

import com.huawei.script.exec.ExecResult;
import com.huawei.script.exec.log.LogCallBack;
import com.huawei.script.exec.session.ServerInfo;
import com.huawei.script.exec.session.ServerSessionFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

/**
 * 用于在远程linux服务器执行本地脚本。
 * <p>需要在远程服务器创建临时脚本，执行完成后删除临时脚本</p>
 *
 * @author y30010171
 * @since 2021-10-20
 **/
@Component
public class RemoteScriptExecutor implements ScriptExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteScriptExecutor.class);

    @Autowired
    private ServerSessionFactory serverSessionFactory;

    @Resource(name = "timeoutScriptExecThreadPool")
    private ThreadPoolExecutor timeoutScriptExecThreadPool;

    @Value("${script.location}")
    private String scriptLocation;

    @Override
    public String mode() {
        return REMOTE;
    }

    @Override
    public ExecResult execScript(ScriptExecInfo scriptExecInfo, LogCallBack logCallback) {
        if (scriptExecInfo.getRemoteServerInfo() == null) {
            throw new IllegalArgumentException("need server info to exec remote script.");
        }
        Session session = null;
        String fileName = "";
        try {
            session = serverSessionFactory.getSession(scriptExecInfo.getRemoteServerInfo());
            ExecResult uploadFileResult =
                uploadFile(session, scriptExecInfo.getScriptName(), scriptExecInfo.getScriptContent());
            if (!uploadFileResult.isSuccess()) {
                LOGGER.error("Failed to upload script. {}", uploadFileResult.getMsg());
                return uploadFileResult;
            }
            fileName = uploadFileResult.getMsg();
            return exec(session, commands("sh", fileName, scriptExecInfo.getParams()), logCallback,
                scriptExecInfo.getDetailId(), scriptExecInfo.getTimeOut());
        } catch (JSchException | IOException | SftpException e) {
            LOGGER.error("Can't get remote server session.", e);
            return ExecResult.error(e.getMessage());
        } finally {
            if (session != null && StringUtils.isNotEmpty(fileName)) {
                deleteFile(session, fileName);
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    @Override
    public ExecResult cancel(ServerInfo serverInfo, int pid) {
        if (serverInfo == null) {
            throw new IllegalArgumentException("need server info to cancel.");
        }
        Session session;
        try {
            session = serverSessionFactory.getSession(serverInfo);
            return exec(session, commands("kill", String.format(Locale.ROOT, "-9 %s", pid), null), null, -1);
        } catch (JSchException e) {
            LOGGER.error("Can't get remote server session.", e);
            return ExecResult.fail(e.getMessage());
        }
    }

    public ExecResult uploadFile(Session session, String uploadPath, File file)
        throws JSchException, IOException, SftpException {
        ChannelSftp channel = null;
        try (FileInputStream inputStream = new FileInputStream(file)
        ) {
            ExecResult createDirResult = createRemoteDir(session, uploadPath);
            if (!createDirResult.isSuccess()) {
                LOGGER.error("Failed to create dir {}. {}", uploadPath, createDirResult.getMsg());
                return createDirResult;
            }
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.setInputStream(inputStream);
            long startUpload = System.currentTimeMillis();
            channel.connect();
            String fileName = uploadPath + file.getName();
            channel.put(inputStream, fileName);
            LOGGER.debug("upload file {} to {} cost {} ms", file.getPath(), fileName,
                System.currentTimeMillis() - startUpload);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
        return ExecResult.success("");
    }

    private ExecResult uploadFile(Session session, String scriptName, String scriptContent)
        throws JSchException, IOException, SftpException {
        ChannelSftp channel = null;
        String fileName = String.format(Locale.ROOT, "%s%s-%s.sh",
            scriptLocation, scriptName, System.currentTimeMillis());
        try (BufferedInputStream inputStream = new BufferedInputStream(
            new ByteArrayInputStream(scriptContent.getBytes(StandardCharsets.UTF_8)))
        ) {
            ExecResult createDirResult = createRemoteDir(session, scriptLocation);
            if (!createDirResult.isSuccess()) {
                LOGGER.error("Failed to create dir {}. {}", scriptLocation, createDirResult.getMsg());
                return createDirResult;
            }
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.setInputStream(inputStream);
            channel.connect();
            channel.put(inputStream, fileName);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
        return ExecResult.success(fileName);
    }

    private ExecResult deleteFile(Session session, String fileName) {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            channel.rm(fileName);
            LOGGER.debug("script file {} was deleted.", fileName);
        } catch (JSchException | SftpException e) {
            LOGGER.error("Failed to delete file {}.{}", fileName, e.getMessage());
            ExecResult.error(e.getMessage());
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
        return ExecResult.success("");
    }

    /**
     * 在远程服务器上创建文件夹
     *
     * @param session 远程连接会话
     * @param remoteDirLocation 远程文件夹路径
     */
    private ExecResult createRemoteDir(Session session, String remoteDirLocation) {
        String command = String.format(Locale.ROOT, "mkdir -p %s", remoteDirLocation);
        return exec(session, command, null, 0);
    }

    public ExecResult exec(Session session, String command, LogCallBack logCallback, int id) {
        return exec(session, command, logCallback, id, 0);
    }

    /**
     * 执行远程服务器命令
     *
     * @param session 远程服务器连接会话
     * @param command 命令
     * @param logCallback 日志回调
     * @param id 标识本次执行的关键字
     * @param timeOut 超时时间
     * @return {@link ExecResult} 执行结果
     */
    public ExecResult exec(Session session, String command, LogCallBack logCallback, int id, long timeOut) {
        ChannelExec channel = null;
        Future<ExecResult> task = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            long startTime = System.currentTimeMillis();
            ExecResult execResult;
            channel.connect();
            if (timeOut > 0) {
                ChannelExec finalChannel = channel;
                task = timeoutScriptExecThreadPool.submit(() -> parseResult(finalChannel, logCallback, id));
                execResult = task.get(timeOut, TimeUnit.MILLISECONDS);
            } else {
                execResult = parseResult(channel, logCallback, id);
            }
            LOGGER.debug("exec command {} cost {}ms", command, System.currentTimeMillis() - startTime);
            return execResult;
        } catch (IOException e) {
            LOGGER.error("Failed to get exec result.", e);
            return ExecResult.error(e.getMessage());
        } catch (JSchException e) {
            LOGGER.error("Access remote server session error.", e);
            return ExecResult.error(e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("exec {} error. {}", id, e.getMessage());
            return ExecResult.error(e.getMessage());
        } catch (TimeoutException e) {
            LOGGER.error("exec {} was timeout. {}", id, e.getMessage());
            return ExecResult.error("time out");
        } finally {
            if (task != null) {
                task.cancel(true);
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    /**
     * 解析远程服务器返回的消息
     *
     * @param channel 通道
     * @param logCallback 处理日志回调
     * @param id 标识本次执行的关键字
     * @return String 结果
     * @throws IOException
     */
    private ExecResult parseResult(Channel channel, LogCallBack logCallback, int id) throws IOException {
        ExecResult execResult = new ExecResult();
        BufferedReader normalInfoReader = new BufferedReader(
            new InputStreamReader(channel.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        while (true) {
            String line;
            while ((line = normalInfoReader.readLine()) != null) {
                if (logCallback != null && id > 0) {
                    logCallback.handleLog(id, line);
                }
                result.append(line).append(System.lineSeparator());
            }

            // 命令执行完毕
            if (channel.isClosed()) {
                if (channel.getInputStream().available() > 0) {
                    continue;
                }
                break;
            }
        }
        execResult.setCode(channel.getExitStatus());
        execResult.setMsg(result.toString());
        return execResult;
    }

    private String commands(String type, String fileName, String[] params) {
        StringBuilder result = new StringBuilder(fileName);
        if (params != null) {
            for (String param : params) {
                result.append(" ").append(param);
            }
        }
        return String.format(Locale.ROOT, "%s %s 2>&1", type, result);
    }
}
