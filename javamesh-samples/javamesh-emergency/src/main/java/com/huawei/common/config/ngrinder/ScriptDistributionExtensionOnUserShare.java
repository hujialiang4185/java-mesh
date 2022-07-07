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
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * 压测任务打包下发时，处理用户共享目录下的lib和resources
 *
 * @author y30010171
 * @since 2022-06-25
 **/
@Component
public class ScriptDistributionExtensionOnUserShare implements ScriptDistributionExtension {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptDistributionExtensionOnUserShare.class);

    @Autowired
    private NfsFileEntryService nfsFileEntryService;

    @PostConstruct
    public void init() {
        ScriptHandler.SCRIPT_DISTRIBUTION_EXTENSIONS.add(this);
        LOGGER.info("Add UserShare ScriptDistributionExtension to ScriptHandler");
    }

    @Override
    public List<FileEntry> extensionLibAndResources(ScriptHandler scriptHandler, File distDir, Long testId, User user,
        FileEntry scriptEntry) throws IOException {
        String changeUserPath = scriptHandler.getBasePath(scriptEntry.getPath());
        List<FileEntry> fileList = newArrayList();
        for (FileEntry eachFileEntry : nfsFileEntryService.getUserScriptAllFiles(user, "lib/")) {
            // Skip jython 2.5... it's already included.
            if (startsWithIgnoreCase(eachFileEntry.getFileName(), "jython-2.5.")
                || startsWithIgnoreCase(eachFileEntry.getFileName(), "jython-standalone-2.5.")) {
                continue;
            }
            FileType fileType = eachFileEntry.getFileType();
            if (fileType.isLibDistributable()) {
                eachFileEntry.setPath(changeUserPath + eachFileEntry.getPath()); // add to lib path
                fileList.add(eachFileEntry);
            }
        }
        for (FileEntry eachFileEntry : nfsFileEntryService.getUserScriptAllFiles(user, "resources/")) {
            FileType fileType = eachFileEntry.getFileType();
            if (fileType.isResourceDistributable()) {
                eachFileEntry.setPath(changeUserPath + eachFileEntry.getPath()); // add to resources path
                fileList.add(eachFileEntry);
            }
        }
        return fileList;
    }
}
