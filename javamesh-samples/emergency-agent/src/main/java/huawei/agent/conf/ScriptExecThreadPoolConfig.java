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
 * @author y30010171
 * @since 2022-04-19
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
