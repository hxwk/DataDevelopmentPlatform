package com.dfssi.dataplatform.abs.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/27 19:31
 */
@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {

    @Value("${executor.core.pool.size}")
    private String corePoolSize;

    @Value("${executor.max.pool.size}")
    private String maxPoolSize;

    @Value("${executor.queue.capacity}")
    private String queueCapacit;

    @Value("${executor.thread.name.prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(Integer.valueOf(corePoolSize));
        executor.setMaxPoolSize(Integer.valueOf(maxPoolSize));
        executor.setQueueCapacity(Integer.valueOf(queueCapacit));
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        return executor;
    }
}
