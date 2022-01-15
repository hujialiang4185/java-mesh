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

package com.huawei.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author y30010171
 * @since 2022-01-15
 **/
@Configuration
public class ThreadPoolConfig {

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor sendAgentThreadPool() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            8,
            8,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            new ThreadFactory() {
                private AtomicInteger threadCount = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "sendAgent-" + threadCount.getAndIncrement());
                }
            });
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
}
