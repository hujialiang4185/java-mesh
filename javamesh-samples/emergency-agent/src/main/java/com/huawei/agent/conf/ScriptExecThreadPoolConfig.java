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

package com.huawei.agent.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 脚本执行线程池配置
 *
 * @author h3009881
 * @since 2021-12-17
 **/
@Configuration
public class ScriptExecThreadPoolConfig {
    @Value("${script.executor.corePoolSize}")
    private int coreSize;

    @Value("${script.executor.maxPoolSize}")
    private int maxSize;

    @Value("${script.executor.keepAliveTime}")
    private long keepAliveTime;

    @Value("${script.executor.blockingQueueSize}")
    private int blockingSize;

    /**
     * 用于脚本执行的线程池。
     *
     * @return {@link ThreadPoolExecutor}
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor scriptExecThreadPool() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            coreSize,
            maxSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(blockingSize),
            new ThreadFactory() {
                private AtomicInteger threadCount = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "script-exec-" + threadCount.getAndIncrement());
                }
            });
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
}
