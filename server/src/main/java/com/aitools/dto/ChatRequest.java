package com.aitools.dto;

/**
 * 聊天请求DTO
 */
public class ChatRequest {
    
    private String modelType;
    private String modelName;
    private String question;
    
    public ChatRequest() {}
    
    public ChatRequest(String modelType, String question) {
        this.modelType = modelType;
        this.question = question;
    }
    
    public ChatRequest(String modelType, String modelName, String question) {
        this.modelType = modelType;
        this.modelName = modelName;
        this.question = question;
    }
    
    public String getModelType() {
        return modelType;
    }
    
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
}
