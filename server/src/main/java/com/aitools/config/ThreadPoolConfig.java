package com.aitools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置 - 支持高并发处理
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    
    /**
     * LLM请求处理线程池
     */
    @Bean("llmExecutor")
    public Executor llmExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数 - 根据CPU核心数设置
        int coreThreads = Runtime.getRuntime().availableProcessors() * 2;
        executor.setCorePoolSize(coreThreads);
        
        // 最大线程数
        executor.setMaxPoolSize(coreThreads * 2);
        
        // 队列容量
        executor.setQueueCapacity(1000);
        
        // 线程名前缀
        executor.setThreadNamePrefix("llm-pool-");
        
        // 拒绝策略 - 由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
    
    /**
     * HTTP客户端线程池（已在HttpClientConfig中配置）
     */
}
