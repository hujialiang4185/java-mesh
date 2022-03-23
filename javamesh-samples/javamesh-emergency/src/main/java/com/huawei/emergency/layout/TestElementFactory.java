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

import com.huawei.emergency.layout.assertion.Jsr223Assertion;
import com.huawei.emergency.layout.assertion.ResponseAssertion;
import com.huawei.emergency.layout.config.Counter;
import com.huawei.emergency.layout.config.CsvDataSetConfig;
import com.huawei.emergency.layout.config.DnsCacheManager;
import com.huawei.emergency.layout.config.HttpCacheManager;
import com.huawei.emergency.layout.config.HttpCookieManager;
import com.huawei.emergency.layout.config.HttpHeaderManager;
import com.huawei.emergency.layout.config.HttpRequestDefault;
import com.huawei.emergency.layout.config.KeystoreConfiguration;
import com.huawei.emergency.layout.controller.LoopController;
import com.huawei.emergency.layout.controller.TransactionController;
import com.huawei.emergency.layout.controller.WhileController;
import com.huawei.emergency.layout.custom.AfterProcessTestElement;
import com.huawei.emergency.layout.custom.AfterTestElement;
import com.huawei.emergency.layout.custom.AfterThreadTestElement;
import com.huawei.emergency.layout.custom.BeforeProcessTestElement;
import com.huawei.emergency.layout.custom.BeforeTestElement;
import com.huawei.emergency.layout.custom.BeforeThreadTestElement;
import com.huawei.emergency.layout.custom.CustomCodeTestElement;
import com.huawei.emergency.layout.custom.CustomMethodTestElement;
import com.huawei.emergency.layout.custom.JarImportTestElement;
import com.huawei.emergency.layout.processor.BeanShellPostProcessor;
import com.huawei.emergency.layout.processor.Jsr223PostProcessor;
import com.huawei.emergency.layout.processor.Jsr223PreProcessor;
import com.huawei.emergency.layout.processor.RegularPostProcessor;
import com.huawei.emergency.layout.sampler.HttpSampler;
import com.huawei.emergency.layout.sampler.Jsr223Sampler;
import com.huawei.emergency.layout.time.ConstantTimer;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI测试元件工厂类
 *
 * @author y30010171
 * @since 2021-12-17
 **/
public class TestElementFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestElementFactory.class);
    private static final Collection<Class<?>> GENERAL_CLASS_TYPE;
    private static final String SET_METHOD_PREFIX = "set";
    private static final Map<String, String> ALL_HANDLER_CLASS_NAME = new HashMap<>();
    private static final List<TestElement> DEFAULT_ELEMENTS = new ArrayList<>();

    private TestElementFactory() {
    }

    static {
        GENERAL_CLASS_TYPE = Sets.newHashSet(boolean.class, Boolean.class, int.class, Integer.class, long.class,
            Long.class, String.class, List.class, Map.class);
        ALL_HANDLER_CLASS_NAME.put("Root", TestPlanTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("ConstantTimer", ConstantTimer.class.getName());
        ALL_HANDLER_CLASS_NAME.put("HTTPRequest", HttpSampler.class.getName());
        ALL_HANDLER_CLASS_NAME.put("JARImport", JarImportTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("Jsr223Sampler", Jsr223Sampler.class.getName());
        ALL_HANDLER_CLASS_NAME.put("JSR223PreProcessor", Jsr223PreProcessor.class.getName());
        ALL_HANDLER_CLASS_NAME.put("JSR223PostProcessor", Jsr223PostProcessor.class.getName());
        ALL_HANDLER_CLASS_NAME.put("bean_shell_post_processor", BeanShellPostProcessor.class.getName());
        ALL_HANDLER_CLASS_NAME.put("RegularExpressionExtractor", RegularPostProcessor.class.getName());
        ALL_HANDLER_CLASS_NAME.put("After", AfterTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("AfterProcess", AfterProcessTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("AfterThread", AfterThreadTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("Before", BeforeTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("BeforeProcess", BeforeProcessTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("BeforeThread", BeforeThreadTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("custom_code", CustomCodeTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("TestFunc", CustomMethodTestElement.class.getName());
        ALL_HANDLER_CLASS_NAME.put("LoopController", LoopController.class.getName());
        ALL_HANDLER_CLASS_NAME.put("TransactionController", TransactionController.class.getName());
        ALL_HANDLER_CLASS_NAME.put("WhileController", WhileController.class.getName());
        ALL_HANDLER_CLASS_NAME.put("KeystoreConfiguration", KeystoreConfiguration.class.getName());
        ALL_HANDLER_CLASS_NAME.put("http_request_default", HttpRequestDefault.class.getName());
        ALL_HANDLER_CLASS_NAME.put("HTTPHeaderManager", HttpHeaderManager.class.getName());
        ALL_HANDLER_CLASS_NAME.put("HTTPCookieManager", HttpCookieManager.class.getName());
        ALL_HANDLER_CLASS_NAME.put("HttpCacheManager", HttpCacheManager.class.getName());
        ALL_HANDLER_CLASS_NAME.put("dns_cache_manager", DnsCacheManager.class.getName());
        ALL_HANDLER_CLASS_NAME.put("CSVDataSetConfig", CsvDataSetConfig.class.getName());
        ALL_HANDLER_CLASS_NAME.put("Counter", Counter.class.getName());
        ALL_HANDLER_CLASS_NAME.put("ResponseAssertion", ResponseAssertion.class.getName());
        ALL_HANDLER_CLASS_NAME.put("JSR223Assertion", Jsr223Assertion.class.getName());
        initDefaultTemplate();
    }

    public static void initDefaultTemplate() {
        BeforeProcessTestElement beforeProcessTestElement = new BeforeProcessTestElement();
        beforeProcessTestElement.setTitle("进程前置");
        beforeProcessTestElement.setElementType("BeforeProcess");
        DEFAULT_ELEMENTS.add(beforeProcessTestElement);
        BeforeThreadTestElement beforeThreadTestElement = new BeforeThreadTestElement();
        beforeThreadTestElement.setTitle("线程前置");
        beforeThreadTestElement.setElementType("BeforeThread");
        DEFAULT_ELEMENTS.add(beforeThreadTestElement);
        BeforeTestElement beforeTestElement = new BeforeTestElement();
        beforeTestElement.setTitle("事务前置");
        beforeTestElement.setElementType("Before");
        DEFAULT_ELEMENTS.add(beforeTestElement);
        TransactionController transactionController = new TransactionController();
        transactionController.setTitle("事务控制器");
        transactionController.setElementType("TransactionController");
        DEFAULT_ELEMENTS.add(transactionController);
        AfterTestElement afterTestElement = new AfterTestElement();
        afterTestElement.setTitle("事务后置");
        afterTestElement.setElementType("After");
        DEFAULT_ELEMENTS.add(afterTestElement);
        AfterThreadTestElement afterThreadTestElement = new AfterThreadTestElement();
        afterThreadTestElement.setTitle("线程后置");
        afterThreadTestElement.setElementType("AfterThread");
        DEFAULT_ELEMENTS.add(afterThreadTestElement);
        AfterProcessTestElement afterProcessTestElement = new AfterProcessTestElement();
        afterProcessTestElement.setTitle("进程后置");
        afterProcessTestElement.setElementType("AfterProcess");
        DEFAULT_ELEMENTS.add(afterProcessTestElement);
    }

    public static List<TestElement> getDefaultTemplate() {
        return DEFAULT_ELEMENTS;
    }

    @Deprecated
    public static TestElement getHandler(String type, Map<String, Object> stringStringMap) {
        String className = ALL_HANDLER_CLASS_NAME.get(type);
        if (StringUtils.isNotEmpty(className)) {
            try {
                TestElement testElement = (TestElement) Class.forName(className).newInstance();
                for (Map.Entry<String, Object> entry : stringStringMap.entrySet()) {
                    if (StringUtils.isNotEmpty(entry.getKey())) {
                        callSetterMethod(testElement, getSetterMethodName(entry.getKey()),
                            entry.getValue() == null ? null : entry.getValue());
                    }
                }
                return testElement;
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Create  {} handler error. {}", type, e.getMessage());
            } catch (ClassNotFoundException e) {
                LOGGER.error("Can't not found {} handler. {}", type);
            }
        }
        return null;
    }

    public static TestElement getHandler(String type, String jsonStr) {
        String className = ALL_HANDLER_CLASS_NAME.get(type);
        if (StringUtils.isNotEmpty(className)) {
            try {
                return (TestElement) JSONObject.parseObject(jsonStr, Class.forName(className));
            } catch (ClassNotFoundException e) {
                LOGGER.error("Can't not found {} handler. {}", type);
            }
        }
        return null;
    }

    private static String getSetterMethodName(final String propertyName) {
        if (propertyName.contains("_")) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, SET_METHOD_PREFIX + "_" + propertyName);
        }
        return SET_METHOD_PREFIX + String.valueOf(propertyName.charAt(0)).toUpperCase() + propertyName.substring(1);
    }

    private static void callSetterMethod(final TestElement testElement, final String methodName,
        final Object setterValue) {
        for (Class<?> each : GENERAL_CLASS_TYPE) {
            try {
                Method method = testElement.getClass().getMethod(methodName, each);
                if (boolean.class == each || Boolean.class == each) {
                    method.invoke(testElement, Boolean.valueOf(setterValue.toString()));
                } else if (int.class == each || Integer.class == each) {
                    method.invoke(testElement, Integer.parseInt(setterValue.toString()));
                } else if (long.class == each || Long.class == each) {
                    method.invoke(testElement, Long.parseLong(setterValue.toString()));
                } else {
                    method.invoke(testElement, setterValue);
                }
                return;
            } catch (final ReflectiveOperationException ignored) {
            }
        }
    }
}
