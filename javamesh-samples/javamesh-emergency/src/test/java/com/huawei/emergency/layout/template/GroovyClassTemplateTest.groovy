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

package com.huawei.emergency.layout.template

class GroovyClassTemplateTest extends GroovyTestCase {

    static final String FILENAME = "D:\\IdeaProject\\hercules-server\\src\\test\\java\\huawei\\emergency\\service\\impl\\main.groovy";
    static final String COPY_FILENAME = "D:\\IdeaProject\\hercules-server\\src\\test\\java\\huawei\\emergency\\service\\impl\\CopyMain.groovy";


    void "test"() {
        GroovyClassTemplate template = GroovyClassTemplate.create(FILENAME);
        template.setClassName("class CopyMain {");
        GroovyMethodTemplate newMethod = GroovyMethodTemplate.create("    public void say() {")
                .addAnnotation("    @Test")
                .addContent("        println(\"Hello, World!\");")
                .addContent("        println(grinder.threadNumber);")
                .end("    }");
        template.addMethod(newMethod);
        template.print(COPY_FILENAME);
        GroovyClassTemplate groovyClassTemplate = GroovyClassTemplate.template();
        groovyClassTemplate.print(System.out);
    }
}
