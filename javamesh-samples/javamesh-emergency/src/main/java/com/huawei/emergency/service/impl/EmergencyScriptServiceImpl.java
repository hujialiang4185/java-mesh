/*
 * Copyright (C) Ltd. 2021-2022. Huawei Technologies Co., All rights reserved
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

package com.huawei.emergency.service.impl;

import static org.ngrinder.common.util.CollectionUtils.newHashMap;
import static org.ngrinder.common.util.ExceptionUtils.processException;

import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.FailedInfo;
import com.huawei.common.constant.ResultCode;
import com.huawei.common.constant.ScriptLanguageEnum;
import com.huawei.common.constant.ScriptTypeEnum;
import com.huawei.common.constant.ValidEnum;
import com.huawei.common.exception.ApiException;
import com.huawei.common.util.EscapeUtil;
import com.huawei.common.util.FileUtil;
import com.huawei.common.util.PasswordUtil;
import com.huawei.emergency.dto.ArgusScript;
import com.huawei.emergency.dto.ScriptManageDto;
import com.huawei.emergency.entity.EmergencyElement;
import com.huawei.emergency.entity.EmergencyElementExample;
import com.huawei.emergency.entity.EmergencyResource;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.EmergencyScriptExample;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.entity.UserEntity;
import com.huawei.emergency.layout.DefaultElementProcessContext;
import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.TestElement;
import com.huawei.emergency.layout.TestElementFactory;
import com.huawei.emergency.layout.TestPlanTestElement;
import com.huawei.emergency.layout.TreeNode;
import com.huawei.emergency.layout.TreeResponse;
import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.mapper.EmergencyElementMapper;
import com.huawei.emergency.mapper.EmergencyScriptMapper;
import com.huawei.emergency.service.EmergencyExecService;
import com.huawei.emergency.service.EmergencyPlanService;
import com.huawei.emergency.service.EmergencyResourceService;
import com.huawei.emergency.service.EmergencyScriptService;
import com.huawei.script.exec.log.LogResponse;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.ngrinder.common.util.UrlUtils;
import org.ngrinder.model.User;
import org.ngrinder.script.handler.GroovyMavenProjectScriptHandler;
import org.ngrinder.script.handler.ProjectHandler;
import org.ngrinder.script.handler.ScriptHandler;
import org.ngrinder.script.model.FileEntry;
import org.ngrinder.script.service.NfsFileEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

/**
 * 脚本管理service
 *
 * @author h30009881
 * @since 2021-10-14
 */
@Service
@Transactional
@Slf4j
public class EmergencyScriptServiceImpl implements EmergencyScriptService {
    private static final int BUF_SIZE = 1024;
    private static final String TYPE_ZERO = "0";
    private static final String TYPE_ONE = "1";
    private static final String TYPE_TWO = "2";
    private static final String SUCCESS = "success";
    private static final String TYPE_THREE = "3";
    private static final String PUBLIC = "公有";
    private static final String PRIVATE = "私有";
    private static final String NO_PASSWORD = "无";
    private static final String HAVE_PASSWORD = "有";
    private static final String ADD = "新增";
    private static final String APPROVING = "待审核";
    private static final String APPROVED = "已审核";
    private static final String UNAPPROVED = "驳回";
    private static final String AUTH_ADMIN = "admin";
    private static final String AUTH_APPROVER = "approver";
    private static final String APPROVING_STATUS = "1";
    private static final String MODE_TEST = "test";
    private static final String MODE_DEV = "dev";
    private static final String BLANK_SPACE = " ";

    @Autowired
    PasswordUtil passwordUtil;

    @Autowired
    private EmergencyScriptMapper mapper;

    @Autowired
    private EmergencyElementMapper elementMapper;

    @Autowired
    private EmergencyExecService execService;

    @Autowired
    private EmergencyResourceService resourceService;

    @Autowired
    private NfsFileEntryService fileEntryService;

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private EmergencyPlanService planService;

    @Value("${mode}")
    private String mode;

    @Override
    public CommonResult<List<EmergencyScript>> listScript(JwtUser jwtUser, String scriptName, String scriptUser,
        int pageSize, int current, String sorter, String order, String status) {
        UserEntity userEntity = jwtUser.getUserEntity();
        String auth;
        List<String> userAuth = jwtUser.getAuthList();
        if (userAuth.contains(AUTH_ADMIN)) {
            auth = AUTH_ADMIN;
        } else if (userAuth.contains(AUTH_APPROVER)) {
            auth = AUTH_APPROVER;
        } else {
            auth = "";
        }
        String sortType;
        if (StringUtils.isBlank(order)) {
            sortType = "update_time" + System.lineSeparator() + "DESC";
        } else if ("ascend".equals(order)) {
            sortType = sorter + System.lineSeparator() + "ASC";
        } else {
            sortType = sorter + System.lineSeparator() + "DESC";
        }
        String userName = userEntity.getUserName();
        String group = userEntity.getGroup();
        Page<EmergencyScript> pageInfo = PageHelper.startPage(current, pageSize, sortType).doSelectPage(() -> {
            mapper.listScript(userName, auth, EscapeUtil.escapeChar(scriptName), EscapeUtil.escapeChar(scriptUser),
                status, group);
        });
        List<EmergencyScript> emergencyScripts = pageInfo.getResult();
        String scriptStatus;
        for (EmergencyScript script : emergencyScripts) {
            scriptStatus = script.getScriptStatus();
            if (scriptStatus.equals(APPROVING_STATUS) && ("admin".equals(userName)
                || ((userAuth.contains(AUTH_ADMIN) || (userAuth.contains(AUTH_APPROVER)
                && userName.equals(script.getApprover())))
                && group.equals(script.getScriptGroup())))) {
                script.setAuditable(true);
            }
            switch (scriptStatus) {
                case TYPE_ZERO:
                    script.setScriptStatus("unapproved");
                    script.setStatusLabel(ADD);
                    break;
                case TYPE_ONE:
                    script.setScriptStatus("approving");
                    script.setStatusLabel(APPROVING);
                    break;
                case TYPE_TWO:
                    script.setScriptStatus("approved");
                    script.setStatusLabel(APPROVED);
                    break;
                default:
                    script.setScriptStatus("unapproved");
                    script.setStatusLabel(UNAPPROVED);
            }
            ScriptLanguageEnum scriptType = ScriptLanguageEnum.matchByValue(script.getScriptType());
            if (scriptType != null) {
                script.setTypeLabel(scriptType.getView());
            }
        }
        return CommonResult.success(emergencyScripts, (int) pageInfo.getTotal());
    }

    @Override
    public CommonResult deleteScripts(int[] scriptIds) {
        CommonResult result = execService.isFreeScript(scriptIds);
        if (!result.isSuccess()) {
            return result;
        }
        for (int scriptId : scriptIds) {
            resourceService.refreshResource(scriptId, new ArrayList<>());
            deleteGrinderScript(scriptId);
            mapper.deleteByPrimaryKey(scriptId);
        }
        return CommonResult.success();
    }

    @Override
    public void downloadScript(int scriptId, HttpServletResponse response) {
        EmergencyScript emergencyScript = mapper.selectByPrimaryKey(scriptId);
        if (emergencyScript == null) {
            log.error("ScriptId not exists. ");
            return;
        }
        String scriptName = emergencyScript.getScriptName();
        String content = emergencyScript.getContent();
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(scriptName, "UTF-8"));
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            byte[] buf = new byte[BUF_SIZE];
            int length = 0;
            outputStream = response.getOutputStream();
            while ((length = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, length);
            }
        } catch (IOException e) {
            log.error("Download script failed. ");
            throw new ApiException(FailedInfo.DOWNLOAD_SCRIPT_FAIL);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("Close stream failed");
            }
        }
    }

    @Override
    public int uploadScript(UserEntity user, EmergencyScript script, MultipartFile file) {
        if ("undefined".equals(script.getServerIp())) {
            script.setServerIp(null);
        }
        if ("undefined".equals(script.getParam())) {
            script.setParam(null);
        }
        try {
            InputStream inputStream = file.getInputStream();
            String content = FileUtil.streamToString(inputStream);
            script.setContent(content);
            return insertScript(user, script);
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    @Override
    public EmergencyScript selectScript(int scriptId) {
        ScriptManageDto scriptInfo = mapper.getScriptInfo(scriptId);
        try {
            if (scriptInfo.getHavePassword().equals("有") && scriptInfo.getPasswordMode().equals("本地")) {
                scriptInfo.setPassword(passwordUtil.decodePassword(scriptInfo.getPassword()));
            }
            List<EmergencyResource> allResources = resourceService.queryResourceByScriptId(scriptInfo.getScriptId());
            String libs = allResources.stream()
                .filter(resource -> resource.getResourcePath() != null && resource.getResourcePath().contains("lib"))
                .map(resource -> resource.getResourceId() + "/" + resource.getResourceName())
                .collect(Collectors.joining(BLANK_SPACE));
            String resources = allResources.stream()
                .filter(
                    resource -> resource.getResourcePath() != null && resource.getResourcePath().contains("resource"))
                .map(resource -> resource.getResourceId() + "/" + resource.getResourceName())
                .collect(Collectors.joining(BLANK_SPACE));
            if (StringUtils.isNotEmpty(libs)) {
                scriptInfo.setLibs(libs);
            }
            if (StringUtils.isNotEmpty(resources)) {
                scriptInfo.setResources(resources);
            }
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("Failed to decode password. ", e);
        }
        return scriptInfo;
    }

    @Override
    public int insertScript(UserEntity user, EmergencyScript script) {
        if (isParamInvalid(script)) {
            return ResultCode.PARAM_INVALID;
        }
        EmergencyScriptExample example = new EmergencyScriptExample();
        example.createCriteria().andScriptNameEqualTo(script.getScriptName());
        long count = mapper.countByExample(example);
        if (count > 0) {
            return ResultCode.SCRIPT_NAME_EXISTS;
        }
        script.setScriptUser(user.getUserName());
        script.setContent(
            FileUtil.streamToString(new ByteArrayInputStream(script.getContent().getBytes(StandardCharsets.UTF_8))));
        script.setScriptGroup(user.getGroup());
        script.setIsPublic(PRIVATE.equals(script.getIsPublic()) ? TYPE_ZERO : TYPE_ONE);
        script.setHavePassword(HAVE_PASSWORD.equals(script.getHavePassword()) ? TYPE_ONE : TYPE_ZERO);
        ScriptLanguageEnum scriptType = ScriptLanguageEnum.match(script.getScriptType(), ScriptTypeEnum.NORMAL);
        if (scriptType == null) {
            throw new ApiException("请选择正确的脚本语言");
        }
        script.setScriptType(scriptType.getValue());
        updateStatusByMode(script);
        if (StringUtils.isEmpty(script.getSubmitInfo())) {
            script.setSubmitInfo("");
        }
        script.setUpdateTime(Timestamp.from(Instant.now()));
        count = mapper.insertSelective(script);
        if (count != 1) {
            return ResultCode.FAIL;
        }
        freshGrinderScript(script.getScriptId());
        return script.getScriptId();
    }

    @Override
    public int updateScript(EmergencyScript script) {
        if (script.getScriptId() == null || StringUtils.isEmpty(script.getContent())) {
            return ResultCode.PARAM_INVALID;
        }
        EmergencyScript updateScript = new EmergencyScript();
        updateScript.setScriptId(script.getScriptId());
        updateScript.setContent(script.getContent());
        updateScript.setParam(script.getParam());
        updateStatusByMode(updateScript);
        updateScript.setUpdateTime(Timestamp.from(Instant.now()));
        if (script instanceof ScriptManageDto) {
            ScriptManageDto scriptManageDto = (ScriptManageDto) script;
            List<String> resourceList = new ArrayList<>();
            if (StringUtils.isNotEmpty(scriptManageDto.getLibs())) {
                resourceList.addAll(Arrays.asList(scriptManageDto.getLibs().split(BLANK_SPACE)));
            }
            if (StringUtils.isNotEmpty(scriptManageDto.getResources())) {
                resourceList.addAll(Arrays.asList(scriptManageDto.getResources().split(BLANK_SPACE)));
            }
            resourceService.refreshResource(script.getScriptId(), resourceList);
        }
        mapper.updateByPrimaryKeySelective(updateScript);
        freshGrinderScript(script.getScriptId());
        return 1;
    }

    @Override
    public List<String> searchScript(JwtUser jwtUser, String scriptName, String status, String scriptType) {
        UserEntity userEntity = jwtUser.getUserEntity();
        String userName = userEntity.getUserName();
        String auth = jwtUser.getAuthList().contains("admin") ? "admin" : "";
        List<String> scriptTypes =
            ScriptLanguageEnum.matchScriptType(ScriptTypeEnum.match(scriptType, ScriptTypeEnum.NORMAL))
                .stream()
                .map(ScriptLanguageEnum::getValue)
                .collect(Collectors.toList());

        return mapper.searchScript(EscapeUtil.escapeChar(scriptName), userName, auth, status, scriptTypes,
            userEntity.getGroup());
    }

    @Override
    public EmergencyScript getScriptByName(String scriptName) {
        EmergencyScript scriptInfo = mapper.getScriptByName(scriptName);
        try {
            if (scriptInfo.getHavePassword().equals(HAVE_PASSWORD) && scriptInfo.getPasswordMode().equals("local")) {
                scriptInfo.setPassword(passwordUtil.decodePassword(scriptInfo.getPassword()));
            }
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("Failed to decode password. ", e);
        }
        return scriptInfo;
    }

    @Override
    public String submitReview(JwtUser jwtUser, EmergencyScript script) {
        script.setScriptStatus(TYPE_ONE);
        script.setComment("");
        UserEntity userEntity = jwtUser.getUserEntity();
        int count;
        if (jwtUser.getAuthList().contains("admin")
            || userEntity.getUserName().equals(mapper.selectUserById(script.getScriptId()))) {
            count = mapper.updateByPrimaryKeySelective(script);
        } else {
            return FailedInfo.INSUFFICIENT_PERMISSIONS;
        }
        if (count == 1) {
            return SUCCESS;
        } else {
            return FailedInfo.SUBMIT_REVIEW_FAIL;
        }
    }

    @Override
    public int approve(String userName, Map<String, Object> map) {
        String approve = (String) map.get("approve");
        int scriptId = (int) map.get("script_id");
        EmergencyScript script = new EmergencyScript();
        script.setScriptId(scriptId);
        script.setApprover(userName);
        if ("通过".equals(approve)) {
            script.setScriptStatus(TYPE_TWO);
        } else {
            script.setScriptStatus(TYPE_THREE);
            String comment = (String) map.get("comment");
            script.setComment(comment);
        }
        return mapper.updateByPrimaryKeySelective(script);
    }

    public void deleteGrinderScript(int scriptId) {
        EmergencyScript script = mapper.selectByPrimaryKey(scriptId);
        if (script == null) {
            return;
        }
        ScriptLanguageEnum scriptType = ScriptLanguageEnum.matchByValue(script.getScriptType());
        if (scriptType == null || scriptType.getScriptType() == ScriptTypeEnum.NORMAL) {
            return;
        }
        User user = new User();
        user.setUserId(script.getScriptUser());
        try {
            fileEntryService.deleteFile(user, grinderDirPath(script));
        } catch (IOException e) {
            log.error("delete script {} error.", scriptId, e.getMessage());
        }
    }

    public void freshGrinderScript(int scriptId) {
        EmergencyScript script = mapper.selectByPrimaryKey(scriptId);
        if (script == null) {
            return;
        }
        ScriptLanguageEnum scriptType = ScriptLanguageEnum.matchByValue(script.getScriptType());
        if (scriptType == null || scriptType.getScriptType() == ScriptTypeEnum.NORMAL) {
            return;
        }
        if (script.getContent() == null) {
            script.setContent("");
        }
        updateGrinderScript(script); // 更新脚本内容
    }

    /**
     * 更新压测脚本
     *
     * @param script {@link EmergencyScript} 脚本信息
     * @return 是否更新成功
     */
    public boolean updateGrinderScript(EmergencyScript script) {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setCreatedUser(planService.findNgrinderUserByUserId(script.getScriptUser()));
        fileEntry.setContent(script.getContent());
        fileEntry.setContentBytes(script.getContent().getBytes(StandardCharsets.UTF_8));
        fileEntry.setPath(grinderPath(script));
        try {
            fileEntryService.saveFile(fileEntry.getCreatedUser(), fileEntry);
        } catch (IOException e) {
            log.error("update script error.", e);
            return false;
        }
        log.info("update script {}.", fileEntry.getPath());
        return true;
    }

    @Override
    public CommonResult debugScript(int scriptId) {
        EmergencyScript emergencyScript = mapper.selectByPrimaryKey(scriptId);
        if (emergencyScript == null) {
            return CommonResult.failed("请选择正确的脚本");
        }
        return execService.exec(emergencyScript);
    }

    @Override
    public CommonResult debugScriptBeforeSave(String content, String serverName) {
        return execService.debugScript(content, serverName);
    }

    @Override
    public CommonResult debugScriptStop(Integer debugId) {
        return CommonResult.success();
    }

    @Override
    public LogResponse debugLog(int detailId, int lineIndex) {
        return execService.getLog(detailId, lineIndex);
    }

    @Override
    public CommonResult createGuiScript(UserEntity user, EmergencyScript script) {
        if (script == null || StringUtils.isEmpty(script.getScriptName())) {
            return CommonResult.failed("请输入脚本名称");
        }
        if (isScriptNameExist(script.getScriptName())) {
            return CommonResult.failed("存在名称相同的脚本");
        }

        // 生成脚本信息 以及脚本编排信息
        EmergencyScript newScript = new EmergencyScript();
        newScript.setScriptName(script.getScriptName());
        newScript.setIsPublic(TYPE_ONE);
        newScript.setScriptType(ScriptLanguageEnum.GUI.getValue());
        newScript.setSubmitInfo(StringUtils.isEmpty(script.getSubmitInfo()) ? "" : script.getSubmitInfo());
        newScript.setHavePassword(TYPE_ZERO);
        newScript.setContent("");
        newScript.setScriptUser(user.getUserName());
        updateStatusByMode(newScript);
        newScript.setScriptGroup(user.getGroup());
        newScript.setUpdateTime(Timestamp.from(Instant.now()));
        mapper.insertSelective(newScript);
        generateTemplate(newScript); // 生成编排模板
        freshGrinderScript(newScript.getScriptId());
        return CommonResult.success(newScript);
    }

    /**
     * 生成默认的编排模板
     *
     * @param script 脚本信息
     */
    private void generateTemplate(EmergencyScript script) {
        EmergencyElement rootElement = new EmergencyElement();
        rootElement.setElementTitle(script.getScriptName());
        rootElement.setElementType("Root");
        rootElement.setElementNo(System.currentTimeMillis() + "-" + rootElement.getElementType());
        rootElement.setScriptId(script.getScriptId());
        rootElement.setCreateUser(script.getScriptUser());
        rootElement.setSeq(1);
        Map<String, Object> elementParams = new HashMap<>();
        elementParams.put("title", rootElement.getElementTitle());
        rootElement.setElementParams(JSONObject.toJSONString(elementParams));
        elementMapper.insertSelective(rootElement);
        int seq = 1;
        for (TestElement testElement : TestElementFactory.getDefaultTemplate()) {
            EmergencyElement element = new EmergencyElement();
            element.setElementTitle(testElement.getTitle());
            element.setElementType(testElement.getElementType());
            element.setElementNo(System.currentTimeMillis() + "-" + element.getElementType());
            element.setScriptId(script.getScriptId());
            element.setParentId(rootElement.getElementId());
            element.setCreateUser(script.getScriptUser());
            Map<String, Object> params = new HashMap<>();
            params.put("title", testElement.getTitle());
            element.setElementParams(JSONObject.toJSONString(params));
            element.setSeq(seq);
            elementMapper.insertSelective(element);
            seq++;
        }
    }

    @Override
    public CommonResult updateGuiScript(String userName, TreeResponse treeResponse) {
        if (treeResponse.getScriptId() == null || mapper.selectByPrimaryKey(treeResponse.getScriptId()) == null) {
            return CommonResult.failed("请选择脚本");
        }
        EmergencyElementExample currentElementsExample = new EmergencyElementExample();
        currentElementsExample.createCriteria()
            .andIsValidEqualTo(ValidEnum.VALID.getValue())
            .andScriptIdEqualTo(treeResponse.getScriptId());
        EmergencyElement updateElement = new EmergencyElement();
        updateElement.setIsValid(ValidEnum.IN_VALID.getValue());
        elementMapper.updateByExampleSelective(updateElement, currentElementsExample);
        TreeNode rootNode = treeResponse.getTree();
        if (rootNode == null) {
            return CommonResult.success();
        }
        List<String> resourceList = new ArrayList<>();
        updateOrchestrate(userName, treeResponse.getScriptId(), -1, rootNode, treeResponse.getMap(), 1, resourceList);
        resourceService.refreshResource(treeResponse.getScriptId(), resourceList); // 清除资源文件

        // 生成代码
        TestPlanTestElement parse = TreeResponse.parse(treeResponse);
        ElementProcessContext context = new DefaultElementProcessContext();
        try {
            context.setTemplate(GroovyClassTemplate.template());
        } catch (IOException e) {
            throw new ApiException("can't create groovy template.");
        }
        parse.handle(context);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            context.getTemplate().print(outputStream);
            String scriptContent = outputStream.toString();
            EmergencyScript script = new EmergencyScript();
            script.setScriptId(treeResponse.getScriptId());
            script.setContent(scriptContent);
            updateStatusByMode(script);
            script.setUpdateTime(Timestamp.from(Instant.now()));
            mapper.updateByPrimaryKeySelective(script);
            freshGrinderScript(script.getScriptId()); // 更新ngrinder
            ArgusScript argusScript = new ArgusScript();
            argusScript.setPath(treeResponse.getPath());
            argusScript.setScript(scriptContent);
            return CommonResult.success(argusScript);
        } catch (IOException e) {
            log.error("Failed to print script.{}", e);
            return CommonResult.failed("输出编排脚本失败");
        }
    }

    public void updateStatusByMode(EmergencyScript script) {
        // 如果是dev或test则不需要提审审核脚本
        if (MODE_DEV.equals(mode) || MODE_TEST.equals(mode)) {
            script.setScriptStatus(TYPE_TWO);
        } else {
            script.setScriptStatus(TYPE_ZERO);
        }
    }

    /**
     * 处理每一个编排节点
     *
     * @param scriptId 脚本ID
     * @param parentId 父节点ID
     * @param node 当前节点
     * @param map 参数集合
     * @param seq 顺序号
     */
    private void updateOrchestrate(String userName, int scriptId, int parentId, TreeNode node, Map<String, Map> map,
        int seq, List<String> resourceList) {
        EmergencyElement element = new EmergencyElement();
        Map elementParams = map.get(node.getKey());
        if (elementParams != null
            && ("JARImport".equals(node.getType()) || "CSVDataSetConfig".equals(node.getType()))
        ) {
            String filenames = elementParams.getOrDefault("filenames", "").toString();
            if (StringUtils.isEmpty(filenames)) {
                elementParams.remove("filenames"); // 前端会多显示一个空行
            } else {
                resourceList.addAll(Arrays.asList(filenames.split(BLANK_SPACE)));
            }
        }
        element.setElementParams(JSONObject.toJSONString(elementParams));
        element.setSeq(seq);
        if (node.getElementId() == null) {
            element.setElementTitle(node.getTitle());
            element.setElementType(node.getType());
            element.setElementNo(node.getKey());
            element.setScriptId(scriptId);
            if (parentId > 0) {
                element.setParentId(parentId);
            }
            element.setCreateUser(userName);
            elementMapper.insertSelective(element);
        } else {
            if (parentId > 0) {
                element.setParentId(parentId);
            }
            element.setElementId(node.getElementId());
            element.setIsValid(ValidEnum.VALID.getValue());
            elementMapper.updateByPrimaryKeySelective(element);
        }
        if (node.getChildren() == null) {
            return;
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            updateOrchestrate(userName, scriptId, element.getElementId(), node.getChildren().get(i), map, i + 1,
                resourceList);
        }
    }

    @Override
    public CommonResult queryGuiScript(int scriptId) {
        EmergencyElementExample rootElementExample = new EmergencyElementExample();
        rootElementExample.createCriteria()
            .andScriptIdEqualTo(scriptId)
            .andParentIdIsNull()
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        rootElementExample.setOrderByClause("seq");
        List<EmergencyElement> emergencyElements = elementMapper.selectByExample(rootElementExample);
        if (emergencyElements.size() == 0) {
            return CommonResult.success();
        }
        EmergencyElement rootElement = emergencyElements.get(0);
        TreeNode root = new TreeNode();
        root.setElementId(rootElement.getElementId());
        root.setKey(rootElement.getElementNo());
        root.setTitle(rootElement.getElementTitle());
        root.setType(rootElement.getElementType());
        root.setChildren(new ArrayList<>());
        TreeResponse response = new TreeResponse();
        response.setScriptId(scriptId);
        response.setTree(root);
        response.setMap(new HashMap<>());
        response.getMap().put(root.getKey(), JSONObject.parseObject(rootElement.getElementParams(), Map.class));
        handleChildren(root, response.getMap()); // 迭代寻找子节点
        return CommonResult.success(response);
    }

    private void handleChildren(TreeNode parent, Map<String, Map> map) {
        if (parent == null || map == null) {
            return;
        }
        EmergencyElementExample elementExample = new EmergencyElementExample();
        elementExample.createCriteria()
            .andParentIdEqualTo(parent.getElementId())
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        elementExample.setOrderByClause("seq");
        List<EmergencyElement> emergencyElements = elementMapper.selectByExample(elementExample);
        for (EmergencyElement emergencyElement : emergencyElements) {
            TreeNode node = new TreeNode();
            node.setElementId(emergencyElement.getElementId());
            node.setKey(emergencyElement.getElementNo());
            node.setTitle(emergencyElement.getElementTitle());
            node.setType(emergencyElement.getElementType());
            node.setChildren(new ArrayList<>());
            map.put(node.getKey(), JSONObject.parseObject(emergencyElement.getElementParams(), Map.class));
            handleChildren(node, map);
            parent.getChildren().add(node);
        }
    }

    @Override
    public CommonResult createIdeScript(UserEntity user, ScriptManageDto scriptManageDto) {
        if (scriptManageDto == null || StringUtils.isEmpty(scriptManageDto.getScriptName())) {
            return CommonResult.failed("请输入脚本名称");
        }
        if (isScriptNameExist(scriptManageDto.getScriptName())) {
            return CommonResult.failed("存在名称相同的脚本");
        }
        EmergencyScript script = new EmergencyScript();
        script.setScriptName(scriptManageDto.getScriptName());
        script.setIsPublic(TYPE_ONE);
        ScriptLanguageEnum scriptLanguage =
            ScriptLanguageEnum.match(scriptManageDto.getScriptType(), ScriptTypeEnum.IDE);
        if (scriptLanguage == null) {
            return CommonResult.failed("请选择正确的脚本语言类型");
        }
        script.setScriptType(scriptLanguage.getValue());
        script.setSubmitInfo(
            StringUtils.isEmpty(scriptManageDto.getSubmitInfo()) ? "" : scriptManageDto.getSubmitInfo());
        script.setHavePassword(TYPE_ZERO);
        script.setContent(
            generateIdeScript(planService.findNgrinderUserByUserId(user.getUserName()), "", scriptManageDto.getForUrl(),
                script.getScriptName(), scriptLanguage.getLanguage(), scriptManageDto.isHasResource(),
                JSONObject.toJSONString(scriptManageDto)));
        script.setScriptUser(user.getUserName());
        updateStatusByMode(script);
        script.setUpdateTime(Timestamp.from(Instant.now()));
        script.setScriptGroup(user.getGroup());
        mapper.insertSelective(script);
        freshGrinderScript(script.getScriptId());
        return CommonResult.success(script);
    }

    @Override
    public CommonResult updateIdeScript(ScriptManageDto scriptManageDto) {
        return CommonResult.success();
    }

    public boolean isScriptNameExist(String scriptName) {
        EmergencyScriptExample isNameExist = new EmergencyScriptExample();
        isNameExist.createCriteria()
            .andScriptNameEqualTo(scriptName);
        return mapper.countByExample(isNameExist) > 0;
    }

    private boolean isParamInvalid(EmergencyScript script) {
        if ("havePassword".equals(script.getHavePassword()) &&
            (StringUtils.isBlank(script.getPassword()) || StringUtils.isBlank(script.getPasswordMode()))) {
            return true;
        }
        return false;
    }

    @Override
    public String grinderPath(EmergencyScript script) {
        ScriptLanguageEnum scriptLanguageEnum = ScriptLanguageEnum.matchByValue(script.getScriptType());
        return grinderDirPath(script) + File.separator + script.getScriptName() + "." + scriptLanguageEnum.getSuffix();
    }

    @Override
    public String grinderDirPath(EmergencyScript script) {
        return script.getScriptName();
    }

    private String generateIdeScript(org.ngrinder.model.User user, String path, String url, String fileName,
        String scriptType,
        boolean createLibAndResources, String options) {
        String hostIp = "Test1";
        String testUrl = url;
        if (StringUtils.isEmpty(url)) {
            testUrl = StringUtils.defaultIfBlank(url, "http://please_modify_this.com");
        } else {
            hostIp = UrlUtils.getHost(url);
        }
        ScriptHandler scriptHandler = fileEntryService.getScriptHandler(scriptType);
        Map<String, Object> map = newHashMap();
        map.put("url", testUrl);
        map.put("userName", user.getUserName());
        map.put("name", hostIp);
        map.put("options", options);
        if (scriptHandler instanceof ProjectHandler) {
            String extension = scriptHandler.getExtension();
            if (scriptHandler instanceof GroovyMavenProjectScriptHandler) {
                extension = "groovy";
            }
            String scriptContent = getScriptTemplate(map, extension);
            scriptHandler.prepareScriptEnv(user, path, StringUtils.trimToEmpty(fileName), hostIp, testUrl,
                createLibAndResources, scriptContent);
            return scriptContent;
        } else {
            return getScriptTemplate(map, scriptHandler.getExtension());
        }
    }

    private String getScriptTemplate(Map<String, Object> values, String extension) {
        try {
            Template template = freemarkerConfig.getTemplate("basic_template_" + extension + ".ftl");
            StringWriter writer = new StringWriter();
            template.process(values, writer);
            return writer.toString();
        } catch (Exception e) {
            throw processException("Error while fetching the script template.", e);
        }
    }
}
