package com.aitools.llm.impl;

import com.aitools.llm.LLMClient;
import com.aitools.llm.LLMResponse;
import com.aitools.llm.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * OpenAI兼容接口客户端基类 - 优化版本
 */
public abstract class OpenAICompatibleClient implements LLMClient {
    
    protected final String apiKey;
    protected final String model;
    protected final String baseUrl;
    protected final String endpoint;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final HttpClient httpClient;
    
    protected OpenAICompatibleClient(String apiKey, String model, String baseUrl, String endpoint) {
        this(apiKey, model, baseUrl, endpoint, HttpClient.newHttpClient());
    }
    
    protected OpenAICompatibleClient(String apiKey, String model, String baseUrl, String endpoint, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
        this.httpClient = httpClient;
    }
    
    @Override
    public LLMResponse chat(List<Message> messages) {
        try {
            String url = baseUrl + endpoint;
            String requestBody = buildRequestBody(messages);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                try {
                    return parseResponse(response.body());
                } catch (Exception e) {
                    throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
                }
            } else {
                throw new RuntimeException("API调用失败: " + response.statusCode() + " - " + response.body());
            }
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("调用大模型失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 异步调用 - 优化响应速度
     */
    public CompletableFuture<LLMResponse> chatAsync(List<Message> messages) {
        return CompletableFuture.supplyAsync(() -> chat(messages));
    }
    
    /**
     * 构建请求体
     */
    protected String buildRequestBody(List<Message> messages) {
        try {
            var requestBody = new java.util.HashMap<String, Object>();
            requestBody.put("model", model);
            
            var messageList = new java.util.ArrayList<java.util.Map<String, String>>();
            for (Message msg : messages) {
                messageList.add(java.util.Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
            requestBody.put("messages", messageList);
            
            return objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("构建请求体失败", e);
        }
    }
    
    /**
     * 解析响应
     */
    protected abstract LLMResponse parseResponse(String responseBody) throws Exception;
    
    public String getModel() {
        return model;
    }
}
