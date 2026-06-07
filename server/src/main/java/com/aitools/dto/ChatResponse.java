package com.aitools.dto;

/**
 * 聊天响应DTO
 */
public class ChatResponse {
    
    private boolean success;
    private String answer;
    private String model;
    private int tokens;
    private String error;
    
    public ChatResponse() {}
    
    public static ChatResponse success(String answer, String model, int tokens) {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);
        response.setAnswer(answer);
        response.setModel(model);
        response.setTokens(tokens);
        return response;
    }
    
    public static ChatResponse error(String errorMessage) {
        ChatResponse response = new ChatResponse();
        response.setSuccess(false);
        response.setError(errorMessage);
        return response;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getTokens() {
        return tokens;
    }
    
    public void setTokens(int tokens) {
        this.tokens = tokens;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
