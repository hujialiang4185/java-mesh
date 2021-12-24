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

package com.huawei.emergency.layout.controller;

import com.huawei.emergency.layout.TestElement;
import com.huawei.emergency.layout.HandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author y30010171
 * @since 2021-12-14
 **/
@Deprecated
public class IfController implements Controller {

    private static final Pattern compile = Pattern.compile("\\#\\{(\\w+)\\}");


    private String expression;

    public IfController(String expression) {
        this.expression = expression;
    }

    @Override
    public void handle(HandlerContext context) {
    }

    public List<Param> parse() {
        List<Param> list = new ArrayList<>();
        final Matcher matcher = compile.matcher(expression);
        while (matcher.find()) {
            list.add(new Param(matcher.group(0), matcher.group(1)));
        }
        return list;
    }

    public static void main(String[] args) {
        Matcher matcher = compile.matcher("#{a} + #{b} == #{ab} + #{bc}");
        while (matcher.find()) {
            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1));
        }
    }

    @Override
    public List<TestElement> nextElements() {
        return null;
    }

    class Param {
        private String variableStr;
        private String variable;

        public Param(String variableStr, String variable) {
            this.variableStr = variableStr;
            this.variable = variable;
        }
    }
}
