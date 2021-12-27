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

package com.huawei.emergency.layout.template;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 类文件模板
 *
 * @author y30010171
 * @since 2021-12-13
 **/
@Data
public class GroovyClassTemplate {
    public static final String FORMAT = "    ";
    public static final String GROOVY_TEMPLATE = "GroovyTemplate";

    private List<GroovyMethodTemplate> allMethods = new ArrayList<>();
    private List<GroovyFieldTemplate> allFields = new ArrayList<>();

    public static final GroovyMethodTemplate USER_METHOD = new GroovyMethodTemplate();
    public static final GroovyMethodTemplate RUN_THREAD_NUM_METHOD = new GroovyMethodTemplate();
    public static final GroovyMethodTemplate TEST_NUMBER_METHOD = new GroovyMethodTemplate();
    public static final GroovyFieldTemplate TEST_NUMBER_FIELD = GroovyFieldTemplate.create("    public static int testNumber = 0;");

    static {
        USER_METHOD.start("public int getVusers() {", 1)
            .addContent("int totalAgents = Integer.parseInt(grinder.getProperties().get(\"grinder.agents\").toString())", 2)
            .addContent("int totalProcesses = Integer.parseInt(grinder.properties.get(\"grinder.processes\").toString())", 2)
            .addContent("int totalThreads = Integer.parseInt(grinder.properties.get(\"grinder.threads\").toString())", 2)
            .addContent("return totalAgents * totalProcesses * totalThreads", 2)
            .end("}", 1)
            .setMethodName("getVusers");
        RUN_THREAD_NUM_METHOD.start("public int getRunThreadNum() {", 1)
            .addContent("int agentNum = grinder.agentNumber", 2)
            .addContent("int processNum = grinder.processNumber", 2)
            .addContent("int threadNum = grinder.threadNumber", 2)
            .addContent("return  (agentNum + 1) * (processNum + 1) * (threadNum + 1)", 2)
            .end("}", 1)
            .setMethodName("getRunThreadNum");
        TEST_NUMBER_METHOD.start("public static int nextTestNumber() {", 1)
            .addContent("return ++testNumber;", 2)
            .end("}", 1)
            .setMethodName("nextTestNumber");
    }

    private Integer importIndex;
    private List<String> preDeclareClassContent = new ArrayList<>();

    public static GroovyClassTemplate template() throws IOException {
        return create(GroovyClassTemplate.class.getClassLoader().getResourceAsStream(GROOVY_TEMPLATE));
    }

    public static GroovyClassTemplate create(InputStream stream) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String line;
            int index = 0;
            boolean isPreClassDeclare = true;
            int isMethodEnd = 0;
            List<String> currentAnnotations = new ArrayList<>();
            GroovyMethodTemplate currentMethod = null;
            GroovyClassTemplate template = new GroovyClassTemplate();
            while ((line = bufferedReader.readLine()) != null) {

                if (template.importIndex == null && line.trim().startsWith("import")) {
                    template.importIndex = index;
                }
                if (isPreClassDeclare) {
                    template.preDeclareClassContent.add(line);
                    if (line.contains("class ")) {
                        isPreClassDeclare = false;
                    }
                    index++;
                    continue;
                }
                if (line.trim().startsWith("@")) {
                    currentAnnotations.add(line);
                    index++;
                    continue;
                }
                // 处理属性和方法
                if (isMethod(line)) {
                    List<String> annotations = new ArrayList<>();
                    annotations.addAll(currentAnnotations);
                    currentAnnotations.clear();
                    currentMethod = GroovyMethodTemplate.create(line);
                    currentMethod.setAnnotations(annotations);
                    template.allMethods.add(currentMethod);
                    isMethodEnd = -1;
                    index++;
                    continue;
                }
                if (currentMethod != null) {
                    String s1 = line.replaceAll("\\{", "");
                    String s2 = line.replaceAll("\\}", "");
                    isMethodEnd += s1.length() - s2.length();
                    if (isMethodEnd == 0) {
                        currentMethod.end(line);
                        currentMethod = null;
                    } else {
                        currentMethod.addContent(line);
                    }
                } else {
                    if (isField(line)) {
                        template.allFields.add(GroovyFieldTemplate.create(line));
                    }
                }
                index++;
            }
            return template;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

    public static GroovyClassTemplate create(String fileName) throws IOException {
        return create(new FileInputStream(fileName));
    }

    public void setClassName(String classDeclareStr) {
        preDeclareClassContent.add(preDeclareClassContent.size() - 1, classDeclareStr);
        preDeclareClassContent.remove(preDeclareClassContent.size() - 1);
    }

    public void addImport(String importStr) {
        preDeclareClassContent.add(importIndex, importStr);
    }

    public void addMethod(GroovyMethodTemplate method) {
        allMethods.add(method);
    }

    public void addFiled(GroovyFieldTemplate field) {
        allFields.add(field);
    }

    public GroovyMethodTemplate getMethod(String methodName) {
        return allMethods.stream()
            .filter(method -> methodName.equals(method.getMethodName()))
            .findFirst()
            .orElseGet(null);
    }

    public GroovyMethodTemplate getMethodByAnnotation(String annotation) {
        return allMethods.stream()
            .filter(method -> method.getAnnotations().contains(annotation))
            .findFirst()
            .orElseGet(null);
    }

    public GroovyMethodTemplate getTestMethod() {
        return getMethodByAnnotation("    @Test");
    }

    public GroovyMethodTemplate getBeforeProcessMethod() {
        return getMethodByAnnotation("    @BeforeProcess");
    }

    public GroovyMethodTemplate getBeforeThreadMethod() {
        return getMethodByAnnotation("    @BeforeThread");
    }

    public GroovyMethodTemplate getBeforeMethod() {
        return getMethodByAnnotation("    @Before");
    }

    public GroovyMethodTemplate getAfterMethod() {
        return getMethodByAnnotation("    @After");
    }

    public GroovyMethodTemplate getAfterThreadMethod() {
        return getMethodByAnnotation("    @AfterThread");
    }

    public GroovyMethodTemplate getAfterProcessMethod() {
        return getMethodByAnnotation("    @AfterProcess");
    }

    public void print(OutputStream outputStream) throws IOException {
        for (int i = 0; i < preDeclareClassContent.size(); i++) {
            if (preDeclareClassContent.get(i) == null) {
                continue;
            }
            outputStream.write(preDeclareClassContent.get(i).getBytes(StandardCharsets.UTF_8));
            outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(TEST_NUMBER_FIELD.getContent().getBytes(StandardCharsets.UTF_8));
        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < allFields.size(); i++) {
            if (allFields.get(i) == null) {
                continue;
            }
            outputStream.write(allFields.get(i).getContent().getBytes(StandardCharsets.UTF_8));
            outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < allMethods.size(); i++) {
            if (allMethods.get(i) == null) {
                continue;
            }
            allMethods.get(i).print(outputStream);
            outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }
        TEST_NUMBER_METHOD.print(outputStream);
        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        USER_METHOD.print(outputStream);
        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        RUN_THREAD_NUM_METHOD.print(outputStream);
        outputStream.write("}".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public void print(String fileName) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName);
            print(outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * 判断某行字符串是否符合变量属性声明
     *
     * @param fieldStr 属性声明字符串
     * @return
     */
    public static boolean isField(String fieldStr) {
        if (StringUtils.isEmpty(fieldStr) || "}".equals(fieldStr.trim())) {
            return false;
        }
        return !isMethod(fieldStr);
    }

    /**
     * 判断某行字符串是否符合方法声明
     *
     * @param methodStr 方法声明的字符串
     * @return
     */
    public static boolean isMethod(String methodStr) {
        return methodStr.trim().startsWith("public static void")
            || methodStr.trim().startsWith("public void")
            || methodStr.trim().startsWith("static void")
            || methodStr.trim().startsWith("void");
    }

    public boolean containsMethod(String methodName) {
        return allMethods.stream().filter(method -> methodName.equals(method.getMethodName())).count() > 0;
    }
}
