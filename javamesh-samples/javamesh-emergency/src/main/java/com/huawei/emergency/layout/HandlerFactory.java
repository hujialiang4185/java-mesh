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

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
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
import com.huawei.emergency.layout.custom.AfterTestElement;
import com.huawei.emergency.layout.custom.AfterProcessTestElement;
import com.huawei.emergency.layout.custom.AfterThreadTestElement;
import com.huawei.emergency.layout.custom.BeforeTestElement;
import com.huawei.emergency.layout.custom.BeforeProcessTestElement;
import com.huawei.emergency.layout.custom.BeforeThreadTestElement;
import com.huawei.emergency.layout.custom.CustomCodeTestElement;
import com.huawei.emergency.layout.custom.CustomMethodTestElement;
import com.huawei.emergency.layout.processor.BeanShellPostProcessor;
import com.huawei.emergency.layout.processor.Jsr223PostProcessor;
import com.huawei.emergency.layout.processor.Jsr223PreProcessor;
import com.huawei.emergency.layout.sampler.HttpSampler;
import com.huawei.emergency.layout.sampler.Jsr223Sampler;
import com.huawei.emergency.layout.time.ConstantTimer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class HandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerFactory.class);
    private static final Collection<Class<?>> GENERAL_CLASS_TYPE;
    private static final String SET_METHOD_PREFIX = "set";
    private static final Map<String, String> allHandlerClassName = new HashMap<>();
    private static final List<String> DEFAULT_HANDLER = Arrays.asList("BeforeProcess", "BeforeThread", "Before", "TransactionController", "After", "AfterThread", "AfterProcess");

    static {
        GENERAL_CLASS_TYPE = Sets.newHashSet(boolean.class, Boolean.class, int.class, Integer.class, long.class, Long.class, String.class);
        allHandlerClassName.put("Root", TestPlanTestElement.class.getName());
        allHandlerClassName.put("constant_timer", ConstantTimer.class.getName());
        allHandlerClassName.put("http_sampler", HttpSampler.class.getName());
        allHandlerClassName.put("jsr223_sampler", Jsr223Sampler.class.getName());
        allHandlerClassName.put("jsr223_pre_processor", Jsr223PreProcessor.class.getName());
        allHandlerClassName.put("jsr223_post_processor", Jsr223PostProcessor.class.getName());
        allHandlerClassName.put("bean_shell_post_processor", BeanShellPostProcessor.class.getName());
        allHandlerClassName.put("After", AfterTestElement.class.getName());
        allHandlerClassName.put("AfterProcess", AfterProcessTestElement.class.getName());
        allHandlerClassName.put("AfterThread", AfterThreadTestElement.class.getName());
        allHandlerClassName.put("Before", BeforeTestElement.class.getName());
        allHandlerClassName.put("BeforeProcess", BeforeProcessTestElement.class.getName());
        allHandlerClassName.put("BeforeThread", BeforeThreadTestElement.class.getName());
        allHandlerClassName.put("custom_code", CustomCodeTestElement.class.getName());
        allHandlerClassName.put("custom_method", CustomMethodTestElement.class.getName());
        allHandlerClassName.put("loop_controller", LoopController.class.getName());
        allHandlerClassName.put("TransactionController", TransactionController.class.getName());
        allHandlerClassName.put("while_controller", WhileController.class.getName());
        allHandlerClassName.put("Keystore_configuration", KeystoreConfiguration.class.getName());
        allHandlerClassName.put("http_request_default", HttpRequestDefault.class.getName());
        allHandlerClassName.put("http_header_manager", HttpHeaderManager.class.getName());
        allHandlerClassName.put("http_cookie_manager", HttpCookieManager.class.getName());
        allHandlerClassName.put("http_cache_manager", HttpCacheManager.class.getName());
        allHandlerClassName.put("dns_cache_manager", DnsCacheManager.class.getName());
        allHandlerClassName.put("csv_data_set_config", CsvDataSetConfig.class.getName());
        allHandlerClassName.put("counter", Counter.class.getName());
        allHandlerClassName.put("response_assertion", ResponseAssertion.class.getName());
    }

    public static List<String> getDefaultTemplate() {
        return DEFAULT_HANDLER;
    }

    public static TestElement getHandler(String type, Map<String, Object> stringStringMap) {
        String className = allHandlerClassName.get(type);
        if (StringUtils.isNotEmpty(className)) {
            try {
                TestElement testElement = (TestElement) Class.forName(className).newInstance();
                for (Map.Entry<String, Object> entry : stringStringMap.entrySet()) {
                    if (StringUtils.isNotEmpty(entry.getKey())) {
                        callSetterMethod(testElement, getSetterMethodName(entry.getKey()), entry.getValue() == null ? null : entry.getValue().toString());
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

    private static String getSetterMethodName(final String propertyName) {
        if (propertyName.contains("_")) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, SET_METHOD_PREFIX + "_" + propertyName);
        }
        return SET_METHOD_PREFIX + String.valueOf(propertyName.charAt(0)).toUpperCase() + propertyName.substring(1);
    }

    private static void callSetterMethod(final TestElement testElement, final String methodName, final String setterValue) {
        for (Class<?> each : GENERAL_CLASS_TYPE) {
            try {
                Method method = testElement.getClass().getMethod(methodName, each);
                if (boolean.class == each || Boolean.class == each) {
                    method.invoke(testElement, Boolean.valueOf(setterValue));
                } else if (int.class == each || Integer.class == each) {
                    method.invoke(testElement, Integer.parseInt(setterValue));
                } else if (long.class == each || Long.class == each) {
                    method.invoke(testElement, Long.parseLong(setterValue));
                } else {
                    method.invoke(testElement, setterValue);
                }
                return;
            } catch (final ReflectiveOperationException ignored) {
            }
        }
    }
}
