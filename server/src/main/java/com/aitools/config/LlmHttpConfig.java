package com.aitools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LLM HTTP客户端配置
 */
@Configuration
@ConfigurationProperties(prefix = "llm.http")
public class LlmHttpConfig {
    
    private int connectTimeout = 10000;
    private int readTimeout = 60000;
    private int connectionPoolSize = 20;
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }
    
    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }
}
