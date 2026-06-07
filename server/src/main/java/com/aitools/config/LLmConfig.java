package com.aitools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LLM模型配置
 */
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLmConfig {
    
    private ApiKeyConfig minimax = new ApiKeyConfig();
    private ApiKeyConfig glm = new ApiKeyConfig();
    private ApiKeyConfig openai = new ApiKeyConfig();
    private ApiKeyConfig deepseek = new ApiKeyConfig();
    
    public ApiKeyConfig getMinimax() {
        return minimax;
    }
    
    public void setMinimax(ApiKeyConfig minimax) {
        this.minimax = minimax;
    }
    
    public ApiKeyConfig getGlm() {
        return glm;
    }
    
    public void setGlm(ApiKeyConfig glm) {
        this.glm = glm;
    }
    
    public ApiKeyConfig getOpenai() {
        return openai;
    }
    
    public void setOpenai(ApiKeyConfig openai) {
        this.openai = openai;
    }
    
    public ApiKeyConfig getDeepseek() {
        return deepseek;
    }
    
    public void setDeepseek(ApiKeyConfig deepseek) {
        this.deepseek = deepseek;
    }
    
    public static class ApiKeyConfig {
        private String apiKey;
        private String model;
        
        public String getApiKey() {
            return apiKey;
        }
        
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
    }
}
