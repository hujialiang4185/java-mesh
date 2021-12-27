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

package com.huawei.emergency.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.huawei.common.api.CommonResult;
import com.huawei.common.constant.ValidEnum;
import com.huawei.common.filter.UserFilter;
import com.huawei.emergency.dto.ArgusScript;
import com.huawei.emergency.entity.EmergencyElement;
import com.huawei.emergency.entity.EmergencyElementExample;
import com.huawei.emergency.layout.ElementProcessContext;
import com.huawei.emergency.layout.HandlerFactory;
import com.huawei.emergency.layout.TestPlanTestElement;
import com.huawei.emergency.layout.TreeNode;
import com.huawei.emergency.layout.TreeResponse;
import com.huawei.emergency.layout.template.GroovyClassTemplate;
import com.huawei.emergency.mapper.EmergencyElementMapper;
import com.huawei.emergency.service.EmergencyArgusScriptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @author y30010171
 * @since 2021-12-27
 **/
@Service
@Slf4j
public class EmergencyArgusScriptServiceImpl implements EmergencyArgusScriptService {

    @Autowired
    EmergencyElementMapper elementMapper;

    @Override
    public CommonResult createArgusOrchestrate(ArgusScript script) {
        generateTemplate(script.getPath(), UserFilter.users.get().getNickName());
        return CommonResult.success(script);
    }

    @Override
    public CommonResult getArgusOrchestrate(String path) {
        EmergencyElement rootElement;
        if (StringUtils.isEmpty(path)) {
            return CommonResult.failed("请选择压测脚本");
        }
        EmergencyElementExample rootElementExample = new EmergencyElementExample();
        rootElementExample.createCriteria()
            .andArgusPathEqualTo(path)
            .andParentIdIsNull()
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        List<EmergencyElement> emergencyElements = elementMapper.selectByExampleWithBLOBs(rootElementExample);
        if (emergencyElements.size() == 0) {
            rootElement = generateTemplate(path, UserFilter.users.get().getNickName());
        } else {
            rootElement = emergencyElements.get(0);
        }

        TreeNode root = new TreeNode();
        root.setElementId(rootElement.getElementId());
        root.setKey(rootElement.getElementNo());
        root.setTitle(rootElement.getElementTitle());
        root.setType(rootElement.getElementType());
        root.setChildren(new ArrayList<>());
        TreeResponse response = new TreeResponse();
        response.setPath(path);
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
        elementExample.setOrderByClause("seq");
        elementExample.createCriteria()
            .andParentIdEqualTo(parent.getElementId())
            .andIsValidEqualTo(ValidEnum.VALID.getValue());
        List<EmergencyElement> emergencyElements = elementMapper.selectByExampleWithBLOBs(elementExample);
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
    public CommonResult updateArgusOrchestrate(HttpServletRequest request, TreeResponse treeResponse) {
        if (treeResponse.getPath() == null) {
            return CommonResult.failed("请选择脚本");
        }
        // 清除之前的编排关系
        EmergencyElementExample currentElementsExample = new EmergencyElementExample();
        currentElementsExample.createCriteria()
            .andIsValidEqualTo(ValidEnum.VALID.getValue())
            .andArgusPathEqualTo(treeResponse.getPath());
        EmergencyElement updateElement = new EmergencyElement();
        updateElement.setIsValid(ValidEnum.IN_VALID.getValue());
        elementMapper.updateByExampleSelective(updateElement, currentElementsExample);
        TreeNode rootNode = treeResponse.getTree();
        if (rootNode == null) {
            return CommonResult.success();
        }

        // 更新节点内容
        updateChildrenNode(treeResponse.getPath(), -1, rootNode, treeResponse.getMap(), 1);

        // 生成代码
        TestPlanTestElement parse = TreeResponse.parse(treeResponse);
        ElementProcessContext context = new ElementProcessContext();
        try {
            context.setTemplate(GroovyClassTemplate.template());
        } catch (IOException e) {
            throw new RuntimeException("can't create groovy template.");
        }
        parse.handle(context);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            context.getTemplate().print(outputStream);
            String scriptContent = outputStream.toString();
            ArgusScript argusScript = new ArgusScript();
            argusScript.setPath(treeResponse.getPath());
            argusScript.setScript(scriptContent);
            //updateArgusScript(request,treeResponse.getPath(),scriptContent); // 更新压测脚本
            return CommonResult.success(argusScript);
        } catch (IOException e) {
            log.error("Failed to print script.{}", e);
            return CommonResult.failed("输出编排脚本失败");
        } catch (RestClientException e) {
            log.error("Failed to update argus script.{}", e);
            return CommonResult.failed("更新压测脚本失败");
        }
    }

    private void updateChildrenNode(String path, int parentId, TreeNode node, Map<String, Map> map, int seq) {
        EmergencyElement element = new EmergencyElement();
        element.setElementParams(JSONObject.toJSONString(map.get(node.getKey())));
        element.setSeq(seq);
        if (node.getElementId() == null) {
            element.setElementTitle(node.getTitle());
            element.setElementType(node.getType());
            element.setElementNo(node.getKey());
            element.setArgusPath(path);
            if (parentId > 0) {
                element.setParentId(parentId);
            }
            element.setCreateUser(UserFilter.users.get().getNickName());
            elementMapper.insertSelective(element);
        } else {
            element.setElementId(node.getElementId());
            element.setIsValid(ValidEnum.VALID.getValue());
            elementMapper.updateByPrimaryKeySelective(element);
        }
        if (node.getChildren() == null) {
            return;
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            updateChildrenNode(path, element.getElementId(), node.getChildren().get(i), map, i + 1);
        }
    }

    /**
     * 生成默认的编排模板
     * @param path 压测脚本路径
     * @param userName 用户名
     */
    private EmergencyElement generateTemplate(String path, String userName) {
        EmergencyElement rootElement = new EmergencyElement();
        rootElement.setElementTitle(path);
        rootElement.setElementType("Root");
        rootElement.setElementNo(System.currentTimeMillis() + "-" + rootElement.getElementType());
        rootElement.setArgusPath(path);
        rootElement.setCreateUser(userName);
        rootElement.setSeq(1);
        Map<String, Object> elementParams = new HashMap<>();
        elementParams.put("title", rootElement.getElementTitle());
        rootElement.setElementParams(JSONObject.toJSONString(elementParams));
        elementMapper.insertSelective(rootElement);
        int seq = 1;
        for (String handlerType : HandlerFactory.getDefaultTemplate()) {
            EmergencyElement element = new EmergencyElement();
            element.setElementTitle(handlerType);
            element.setElementType(handlerType);
            element.setElementNo(System.currentTimeMillis() + "-" + element.getElementType());
            element.setArgusPath(path);
            element.setParentId(rootElement.getElementId());
            element.setCreateUser(userName);
            Map<String, Object> params = new HashMap<>();
            params.put("title", handlerType);
            element.setElementParams(JSONObject.toJSONString(params));
            element.setSeq(seq);
            elementMapper.insertSelective(element);
            seq++;
        }
        return rootElement;
    }
}
