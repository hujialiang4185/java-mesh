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

package com.huawei.emergency.layout;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author y30010171
 * @since 2021-12-15
 **/
@NoArgsConstructor
@Data
public class TreeResponse {
    private TreeNode tree;
    private Integer scriptId;
    private String path;
    private Map<String, Map> map; // key == tree.key

    public static TestPlanTestElement parse(TreeResponse treeResponse) {
        TreeNode root = treeResponse.getTree();
        if (!"Root".equals(root.getType())) {
            return null;
        }
        TestPlanTestElement testPlan = new TestPlanTestElement();
        traverse(testPlan.nextElements(), root.getChildren(), treeResponse.getMap());
        return testPlan;
    }

    public static void traverse(List<TestElement> testElements, List<TreeNode> treeNodes, Map<String, Map> variables) {
        if (testElements == null || treeNodes == null || variables == null) {
            return;
        }
        for (TreeNode treeNode : treeNodes) {
            TestElement testElement = HandlerFactory.getHandler(treeNode.getType(), JSONObject.toJSONString(variables.get(treeNode.getKey())));
            if (testElement == null) {
                continue;
            }
            testElements.add(testElement);
            if (testElement instanceof ParentTestElement) {
                traverse(((ParentTestElement) testElement).nextElements(), treeNode.getChildren(), variables);
            }
        }
    }
}
