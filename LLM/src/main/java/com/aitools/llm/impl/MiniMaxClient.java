package com.aitools.llm.impl;

import com.aitools.llm.LLMResponse;
import com.aitools.llm.Message;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * MiniMax模型客户端
 */
public class MiniMaxClient extends OpenAICompatibleClient {
    
    private static final String DEFAULT_BASE_URL = "https://api.minimax.chat/v1";
    private static final String ENDPOINT = "/chat/completions";
    
    public MiniMaxClient(String apiKey) {
        this(apiKey, "MiniMax-M3");
    }
    
    public MiniMaxClient(String apiKey, String model) {
        super(apiKey, model, DEFAULT_BASE_URL, ENDPOINT);
    }
    
    public MiniMaxClient(String apiKey, String model, HttpClient httpClient) {
        super(apiKey, model, DEFAULT_BASE_URL, ENDPOINT, httpClient);
    }
    
    @Override
    public LLMResponse chat(List<Message> messages) {
        try {
            String url = baseUrl + endpoint;
            String requestBody = buildRequestBody(messages);
            
            // 调试信息
            System.out.println("[MiniMax] 请求URL: " + url);
            System.out.println("[MiniMax] API Key长度: " + (apiKey != null ? apiKey.length() : 0));
            System.out.println("[MiniMax] 请求体(JSON): " + requestBody);
            
            // MiniMax使用Bearer + API Key的格式
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("[MiniMax] 响应状态: " + response.statusCode());
            System.out.println("[MiniMax] 响应体: " + response.body());
            
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
    
    @Override
    protected LLMResponse parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        String content = root.path("choices").get(0).path("message").path("content").asText();
        
        // 移除 MiniMax 返回的 <think> 标签及其内容
        content = removeThinkTag(content);
        
        int tokens = root.path("usage").path("total_tokens").asInt(0);
        return new LLMResponse(content, model, tokens);
    }
    
    /**
     * 移除响应内容中的 <think> 标签及其内容
     * @param content 原始响应内容
     * @return 清理后的内容
     */
    private String removeThinkTag(String content) {
        if (content == null) {
            return null;
        }
        // 使用正则表达式移除 <think>...</think> 标签及其内容（支持多行）
        // [\\s\\S]*? 匹配包括换行符在内的任何字符
        return content.replaceAll("<think>[\\s\\S]*?</think>", "").trim();
    }
}
