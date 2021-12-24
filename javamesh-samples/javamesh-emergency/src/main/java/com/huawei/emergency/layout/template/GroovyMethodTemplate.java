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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author y30010171
 * @since 2021-12-13
 **/
@Data
public class GroovyMethodTemplate {
    private List<String> annotations = new ArrayList<>();
    private String header = "";
    private String methodName;
    private String returnType;
    private boolean isStatic;
    private String end = "    }";
    private List<String> argumentType = new ArrayList<>();
    private List<String> arguments = new ArrayList<>();
    private List<String> content = new ArrayList<>(); // 该方法的所有内容，不包括注解

    public static GroovyMethodTemplate create(String start) {
        return new GroovyMethodTemplate().start(start);
    }

    public GroovyMethodTemplate addAnnotation(String annotationStr) {
        annotations.add(annotationStr);
        return this;
    }

    public GroovyMethodTemplate addContent(String contentStr) {
        return addContent(contentStr, 0);
    }

    public GroovyMethodTemplate addContent(String contentStr, int formatNums) {
        this.content.add(format(contentStr, formatNums));
        return this;
    }

    public GroovyMethodTemplate start(String header) {
        return this.start(header, 0);
    }

    public GroovyMethodTemplate start(String header, int formatNums) {
        this.header = format(header, formatNums);
        return this;
    }

    public GroovyMethodTemplate end(String end) {
        return this.end(end, 0);
    }

    public GroovyMethodTemplate end(String end, int formatNums) {
        this.end = format(end, formatNums);
        return this;
    }

    public String format(String source, int formatNums) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < formatNums; i++) {
            temp.append(GroovyClassTemplate.FORMAT);
        }
        return temp.append(source).toString();
    }

    public String invokeStr() {
        return methodName + "()";
    }


    public void print(OutputStream outputStream) throws IOException {
        for (int i = 0; i < annotations.size(); i++) {
            outputStream.write(annotations.get(i).getBytes(StandardCharsets.UTF_8));
            outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(this.header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < content.size(); i++) {
            outputStream.write(content.get(i).getBytes(StandardCharsets.UTF_8));
            outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(this.end.getBytes(StandardCharsets.UTF_8));
        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
    }
}
