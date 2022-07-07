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

package com.huawei.common.config.ngrinder;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;
import static org.ngrinder.common.util.CollectionUtils.newArrayList;

import com.huawei.argus.serializer.ZipFileUtil;
import com.huawei.common.constant.ScriptLanguageEnum;
import com.huawei.common.constant.ScriptTypeEnum;
import com.huawei.emergency.entity.EmergencyExecRecord;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.mapper.EmergencyExecRecordDetailMapper;
import com.huawei.emergency.service.EmergencyPlanService;
import com.huawei.emergency.service.EmergencyScriptService;
import com.huawei.emergency.service.EmergencyTaskService;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.ngrinder.model.User;
import org.ngrinder.script.handler.ScriptDistributionExtension;
import org.ngrinder.script.handler.ScriptHandler;
import org.ngrinder.script.model.FileEntry;
import org.ngrinder.script.model.FileType;
import org.ngrinder.script.service.NfsFileEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

/**
 * 压测任务打包下发时，处理共享任务下的lib和resources
 *
 * @author y30010171
 * @since 2022-06-25
 **/
@Component
public class ScriptDistributionExtensionOnTaskShare implements ScriptDistributionExtension {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptDistributionExtensionOnTaskShare.class);

    @Autowired
    private NfsFileEntryService nfsFileEntryService;

    @Autowired
    private EmergencyExecRecordDetailMapper recordDetailMapper;

    @Autowired
    private EmergencyTaskService taskService;

    @Autowired
    private EmergencyPlanService planService;

    @Autowired
    private EmergencyScriptService scriptService;

    @PostConstruct
    public void init() {
        ScriptHandler.SCRIPT_DISTRIBUTION_EXTENSIONS.add(this);
        LOGGER.info("Add TaskShare ScriptDistributionExtension to ScriptHandler");
    }

    @Override
    public List<FileEntry> extensionLibAndResources(ScriptHandler scriptHandler, File distDir, Long testId,
        User user,
        FileEntry scriptEntry) throws IOException {
        EmergencyExecRecord execRecord = recordDetailMapper.selectRecordByPerfTestId(testId);
        if (execRecord == null) {
            LOGGER.error("Can't find valid exec record by perfTestId {}", testId);
            return Collections.emptyList();
        }
        if (!taskService.isTaskShared(execRecord.getTaskId())) {
            return Collections.emptyList();
        }
        List<FileEntry> fileList = new ArrayList<>();
        List<FileEntry> subScriptFileList = new ArrayList<>();
        String redirectUserPath = scriptHandler.getBasePath(scriptEntry.getPath());
        for (EmergencyScript script : taskService.getAllScriptOnTaskShared(execRecord.getTaskId())) {
            ScriptLanguageEnum scriptLanguageEnum = ScriptLanguageEnum.matchByValue(script.getScriptType());
            if (scriptLanguageEnum.getScriptType() == ScriptTypeEnum.NORMAL) {
                continue;
            }
            FileEntry subScriptEntry = nfsFileEntryService.getSpecifyScript(user, scriptService.grinderPath(script));
            fileList.addAll(getAllLibAndResources(redirectUserPath,
                planService.findNgrinderUserByUserId(script.getScriptUser()), subScriptEntry));
            String subScriptPath = FilenameUtils.getPath(subScriptEntry.getPath());
            subScriptEntry.setPath(
                subScriptEntry.getPath()
                    .replaceFirst(FilenameUtils.getPathNoEndSeparator(subScriptPath), redirectUserPath));
            subScriptFileList.add(subScriptEntry); // 将脚本先加入到目录中
        }
        createJarAndMoveToLibDir(scriptHandler, distDir, testId, subScriptFileList, scriptEntry);
        return fileList;
    }

    /**
     * 将子任务的脚本文件打包成jar并移动到dist/lib 路径下
     *
     * @param scriptHandler
     * @param distDir dist目录
     * @param testId 压测任务id
     * @param subScriptFileList 子任务的脚本集合
     * @param scriptEntry 压测任务脚本
     * @throws IOException 文件找不到或无法创建
     */
    public void createJarAndMoveToLibDir(ScriptHandler scriptHandler, File distDir, Long testId,
        List<FileEntry> subScriptFileList, FileEntry scriptEntry) throws IOException {
        if (subScriptFileList == null || subScriptFileList.size() == 0) {
            return;
        }
        String basePath = scriptHandler.getBasePath(scriptEntry.getPath());
        File jarPackageDir = new File(distDir, File.separatorChar + "resources");
        int count = 1;
        for (FileEntry eachSubScript : subScriptFileList) {
            if (eachSubScript.getFileType() == FileType.DIR) {
                continue;
            }
            String packageStr = String.format(Locale.ROOT, "package share%s" + System.lineSeparator(), count);
            eachSubScript.setContent(packageStr + eachSubScript.getContent());
            eachSubScript.setContentBytes(
                ArrayUtils.addAll(packageStr.getBytes(StandardCharsets.UTF_8), eachSubScript.getContentBytes()));
            File toDir = new File(jarPackageDir, this.calcDistSubPath(basePath, eachSubScript));
            LOGGER.info("{} is being written in {} for test {}", eachSubScript.getPath(), toDir, testId);
            nfsFileEntryService.writeContentTo(eachSubScript, toDir);
            count++;
        }
        Path jarFilePath = Paths.get(jarPackageDir.getParent(), "sub-script.jar");
        try {
            ZipFileUtil.jarFile(jarFilePath.toString(), jarPackageDir);
            ZipFileUtil.deleteChildFile(jarPackageDir);
            File distLibDir = new File(distDir, File.separatorChar + "lib");
            if (!distLibDir.exists()) {
                distLibDir.mkdir();
            }
            Files.copy(jarFilePath, Paths.get(distLibDir.getPath(), "sub-script.jar"));
            LOGGER.info("add {} to {} for test {}", jarFilePath.getFileName(), distLibDir.getPath(), testId);
        } finally {
            Files.deleteIfExists(jarFilePath);
        }
    }

    /**
     * 获取user目录下脚本名称为scriptName的脚本的lib以及resources 并重定向他们的path为redirectUserPath
     *
     * @param redirectUserPath 重定向的打包路径
     * @param user 脚本用户
     * @param scriptEntry 脚本
     * @return 资源集合
     * @throws IOException 文件找不到
     */
    public List<FileEntry> getAllLibAndResources(String redirectUserPath, User user, FileEntry scriptEntry)
        throws IOException {
        List<FileEntry> fileList = newArrayList();
        String path = FilenameUtils.getPath(scriptEntry.getPath());
        for (FileEntry eachFileEntry : nfsFileEntryService.getUserScriptAllFiles(user,
            path + "lib/")) {
            // Skip jython 2.5... it's already included.
            if (startsWithIgnoreCase(eachFileEntry.getFileName(), "jython-2.5.")
                || startsWithIgnoreCase(eachFileEntry.getFileName(), "jython-standalone-2.5.")) {
                continue;
            }
            FileType fileType = eachFileEntry.getFileType();
            if (fileType.isLibDistributable()) {
                eachFileEntry.setPath(eachFileEntry.getPath().replaceFirst(FilenameUtils.getPathNoEndSeparator(path),
                    redirectUserPath));
                fileList.add(eachFileEntry);
            }
        }
        for (FileEntry eachFileEntry : nfsFileEntryService.getUserScriptAllFiles(user,
            path + "resources/")) {
            FileType fileType = eachFileEntry.getFileType();
            if (fileType.isResourceDistributable()) {
                eachFileEntry.setPath(
                    eachFileEntry.getPath().replaceFirst(FilenameUtils.getPathNoEndSeparator(path), redirectUserPath));
                fileList.add(eachFileEntry);
            }
        }
        return fileList;
    }

    /**
     * Get the appropriated distribution path for the given file entry.
     *
     * @param basePath distribution base path
     * @param fileEntry fileEntry to be distributed
     * @return the resolved destination path.
     */
    public String calcDistSubPath(String basePath, FileEntry fileEntry) {
        String path = FilenameUtils.getPath(fileEntry.getPath());
        if (StringUtils.isEmpty(basePath)) {
            return path;
        }
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (!path.startsWith(File.separator)) {
            path = File.separator + path;
        }
        if (path.length() <= basePath.length()) {
            return "";
        }
        return path.substring(basePath.length());
    }
}
