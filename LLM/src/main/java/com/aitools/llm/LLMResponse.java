package com.aitools.llm;

/**
 * LLM响应封装
 */
public class LLMResponse {
    
    private String content;
    private String model;
    private int tokens;
    
    public LLMResponse() {}
    
    public LLMResponse(String content, String model, int tokens) {
        this.content = content;
        this.model = model;
        this.tokens = tokens;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getUsageTokens() {
        return tokens;
    }
    
    public void setTokens(int tokens) {
        this.tokens = tokens;
    }
}
