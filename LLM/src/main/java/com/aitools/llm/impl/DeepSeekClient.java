package com.aitools.llm.impl;

import com.aitools.llm.LLMResponse;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.HttpClient;

/**
 * DeepSeek客户端
 */
public class DeepSeekClient extends OpenAICompatibleClient {
    
    private static final String DEFAULT_BASE_URL = "https://api.deepseek.com/v1";
    private static final String ENDPOINT = "/chat/completions";
    
    public DeepSeekClient(String apiKey) {
        this(apiKey, "deepseek-chat");
    }
    
    public DeepSeekClient(String apiKey, String model) {
        super(apiKey, model, DEFAULT_BASE_URL, ENDPOINT);
    }
    
    public DeepSeekClient(String apiKey, String model, HttpClient httpClient) {
        super(apiKey, model, DEFAULT_BASE_URL, ENDPOINT, httpClient);
    }
    
    @Override
    protected LLMResponse parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        String content = root.path("choices").get(0).path("message").path("content").asText();
        int tokens = root.path("usage").path("total_tokens").asInt(0);
        return new LLMResponse(content, model, tokens);
    }
}
