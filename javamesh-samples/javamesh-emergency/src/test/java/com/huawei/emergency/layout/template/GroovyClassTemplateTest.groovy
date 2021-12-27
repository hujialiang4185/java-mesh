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

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.type.VoidType
import org.codehaus.groovy.antlr.GroovySourceAST
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDocAssembler

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

    void "test javaParser"() {
        CompilationUnit unit = new CompilationUnit();
        unit.setPackageDeclaration("com.huawei.common.util");
        ClassOrInterfaceDeclaration main = unit.addClass("Main").setPublic(true);
        MethodDeclaration mainMethod = main.addMethod("main", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
        mainMethod.setType(new VoidType());
        mainMethod.addParameter(String[].class,"args");
        BlockStmt body = new BlockStmt();
        body.addStatement("System.out.println(\"Hello, World!\");");
        mainMethod.setBody(body);
        JavadocComment javadocComment = new JavadocComment("test");
        mainMethod.setJavadocComment(javadocComment);
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\IdeaProject\\hercules-server\\src\\main\\java\\com\\huawei\\common\\util\\Main.groovy");
        fileOutputStream.write(unit.toString().getBytes(StandardCharsets.UTF_8));
        fileOutputStream.flush();
        fileOutputStream.close();

        CompilationUnit parse = StaticJavaParser.parse(new File("D:\\IdeaProject\\hercules-server\\src\\test\\java\\huawei\\emergency\\service\\impl\\CopyMain.groovy"));
        parse.getClassByName("CopyMain").get()
                .addMethod("test1", Modifier.Keyword.PUBLIC)
                .setType(new VoidType())
                .addAnnotation("Test")
                .setBody(new BlockStmt().addStatement("System.out.println(\"Hello, World!\");"));
        System.out.println(parse);

        SimpleGroovyClassDocAssembler assembler = new SimpleGroovyClassDocAssembler("",
                "D:\\IdeaProject\\hercules-server\\src\\test\\java\\huawei\\emergency\\service\\impl\\CopyMain.groovy"
                , null, null, null, true);
        GroovySourceAST groovySourceAST = new GroovySourceAST();
        assembler.visitImport(groovySourceAST, Visitor.OPENING_VISIT);
        System.out.println(groovySourceAST);
    }
}
