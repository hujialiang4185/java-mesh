/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置
 *
 * @author y30010171
 * @since 2022-01-15
 **/
@Configuration
public class ThreadPoolConfig {
    private static final long KEEP_ALIVE_TIME = 60L;

    @Value("${script.executor.maxTaskSize}")
    private int maxTaskSize;

    @Value("${script.executor.maxSubtaskSize}")
    private int maxSubtaskSize;

    @Value("${script.executor.blockingTaskSize}")
    private int blockingTaskSize;

    /**
     * 用于预案，任务，脚本执行的线程池 设置核心线程数与最大线程数一致，使得创建的线程与线程名一致。 每个脚本在不同服务器执行时，根据此线程名去获取一个线程池来并发执行。
     *
     * @return {@link ThreadPoolExecutor}
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor scriptExecThreadPool() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            maxTaskSize,
            maxTaskSize,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(blockingTaskSize),
            new ThreadFactory() {
                private AtomicInteger threadCount = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "task-exec-" + threadCount.getAndIncrement());
                }
            });
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    /**
     * 通过此线程池执行设置了超时时间的脚本
     *
     * @return {@link ThreadPoolExecutor}
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor timeoutScriptExecThreadPool() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            maxTaskSize * maxSubtaskSize,
            maxTaskSize * maxSubtaskSize,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(blockingTaskSize),
            new ThreadFactory() {
                private AtomicInteger threadCount = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "timeout-exec-" + threadCount.getAndIncrement());
                }
            });
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

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
