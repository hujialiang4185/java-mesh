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
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author y30010171
 * @since 2021-12-17
 **/
@Component
public class TestElementFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestElementFactory.class);
    private static final Collection<Class<?>> GENERAL_CLASS_TYPE;
    private static final String SET_METHOD_PREFIX = "set";
    private static final Map<String, String> allHandlerClassName = new HashMap<>();
    private static final List<String> DEFAULT_HANDLER = Arrays.asList("BeforeProcess", "BeforeThread", "Before",
        "TransactionController", "After", "AfterThread", "AfterProcess");

    static {
        GENERAL_CLASS_TYPE = Sets.newHashSet(boolean.class, Boolean.class, int.class, Integer.class, long.class,
            Long.class, String.class, List.class, Map.class);
        allHandlerClassName.put("Root", TestPlanTestElement.class.getName());
        allHandlerClassName.put("ConstantTimer", ConstantTimer.class.getName());
        allHandlerClassName.put("HTTPRequest", HttpSampler.class.getName());
        allHandlerClassName.put("JARImport", JarImportTestElement.class.getName());
        allHandlerClassName.put("Jsr223Sampler", Jsr223Sampler.class.getName());
        allHandlerClassName.put("JSR223PreProcessor", Jsr223PreProcessor.class.getName());
        allHandlerClassName.put("JSR223PostProcessor", Jsr223PostProcessor.class.getName());
        allHandlerClassName.put("bean_shell_post_processor", BeanShellPostProcessor.class.getName());
        allHandlerClassName.put("RegularExpressionExtractor", RegularPostProcessor.class.getName());
        allHandlerClassName.put("After", AfterTestElement.class.getName());
        allHandlerClassName.put("AfterProcess", AfterProcessTestElement.class.getName());
        allHandlerClassName.put("AfterThread", AfterThreadTestElement.class.getName());
        allHandlerClassName.put("Before", BeforeTestElement.class.getName());
        allHandlerClassName.put("BeforeProcess", BeforeProcessTestElement.class.getName());
        allHandlerClassName.put("BeforeThread", BeforeThreadTestElement.class.getName());
        allHandlerClassName.put("custom_code", CustomCodeTestElement.class.getName());
        allHandlerClassName.put("TestFunc", CustomMethodTestElement.class.getName());
        allHandlerClassName.put("LoopController", LoopController.class.getName());
        allHandlerClassName.put("TransactionController", TransactionController.class.getName());
        allHandlerClassName.put("WhileController", WhileController.class.getName());
        allHandlerClassName.put("KeystoreConfiguration", KeystoreConfiguration.class.getName());
        allHandlerClassName.put("http_request_default", HttpRequestDefault.class.getName());
        allHandlerClassName.put("HTTPHeaderManager", HttpHeaderManager.class.getName());
        allHandlerClassName.put("HTTPCookieManager", HttpCookieManager.class.getName());
        allHandlerClassName.put("HttpCacheManager", HttpCacheManager.class.getName());
        allHandlerClassName.put("dns_cache_manager", DnsCacheManager.class.getName());
        allHandlerClassName.put("CSVDataSetConfig", CsvDataSetConfig.class.getName());
        allHandlerClassName.put("Counter", Counter.class.getName());
        allHandlerClassName.put("ResponseAssertion", ResponseAssertion.class.getName());
        allHandlerClassName.put("JSR223Assertion", Jsr223Assertion.class.getName());
    }

    public static List<String> getDefaultTemplate() {
        return DEFAULT_HANDLER;
    }

    @Deprecated
    public static TestElement getHandler(String type, Map<String, Object> stringStringMap) {
        String className = allHandlerClassName.get(type);
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
        String className = allHandlerClassName.get(type);
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
