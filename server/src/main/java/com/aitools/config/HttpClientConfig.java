package com.aitools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * HTTP客户端配置 - 优化性能（兼容Java 17）
 */
@Configuration
public class HttpClientConfig {
    
    private final LlmHttpConfig llmHttpConfig;
    
    public HttpClientConfig(LlmHttpConfig llmHttpConfig) {
        this.llmHttpConfig = llmHttpConfig;
    }
    
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                // 设置连接超时
                .connectTimeout(Duration.ofMillis(llmHttpConfig.getConnectTimeout()))
                // 启用HTTP/2支持
                .version(HttpClient.Version.HTTP_2)
                // 配置连接池
                .executor(Executors.newFixedThreadPool(llmHttpConfig.getConnectionPoolSize()))
                // 自动重定向
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }
}
