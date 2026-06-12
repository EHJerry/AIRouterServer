package com.aitools.dto;

/**
 * 聊天请求DTO
 * 
 * 请求体格式：
 * {
 *   "modelType": "minimax",
 *   "model": "...",
 *   "message": "..."
 * }
 */
public class ChatRequest {
    
    private String modelType;
    private String modelName;
    private String model;
    private String question;
    private String message;
    
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
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getInput() {
        return message != null && !message.isEmpty() ? message : question;
    }
    
    public String getModelIdentifier() {
        return model != null && !model.isEmpty() ? model : modelName;
    }
}
