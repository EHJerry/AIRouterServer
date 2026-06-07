package com.aitools.service;

import com.aitools.config.LLmConfig;
import com.aitools.dto.ChatRequest;
import com.aitools.dto.ChatResponse;
import com.aitools.llm.LLMClient;
import com.aitools.llm.LLMFactory;
import com.aitools.llm.LLMResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * LLM服务层 - 支持同步和异步处理
 */
@Service
public class LLmService {
    
    private final LLmConfig llmConfig;
    private final HttpClient httpClient;
    
    public LLmService(LLmConfig llmConfig, HttpClient httpClient) {
        this.llmConfig = llmConfig;
        this.httpClient = httpClient;
    }
    
    /**
     * 同步处理聊天请求
     */
    public ChatResponse chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            String modelType = request.getModelType().toLowerCase();
            String question = request.getQuestion();
            
            LLMClient client = createClient(modelType, request.getModelName());
            List<com.aitools.llm.Message> messages = new java.util.ArrayList<>();
            messages.add(new com.aitools.llm.Message("user", question));
            LLMResponse response = client.chat(messages);
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[LLmService] 请求完成 - model: " + response.getModel() + ", tokens: " + response.getUsageTokens() + ", duration: " + duration + "ms");
            
            return ChatResponse.success(
                    response.getContent(),
                    response.getModel(),
                    response.getUsageTokens()
            );
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            System.err.println("[LLmService] 请求失败 - error: " + e.getMessage() + ", duration: " + duration + "ms");
            return ChatResponse.error("调用大模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 异步处理聊天请求（使用线程池）
     */
    @Async("llmExecutor")
    public CompletableFuture<ChatResponse> chatAsync(ChatRequest request) {
        return CompletableFuture.completedFuture(chat(request));
    }
    
    /**
     * 根据模型类型创建客户端（使用优化的HttpClient）
     */
    private LLMClient createClient(String modelType, String customModelName) {
        return switch (modelType) {
            case "minimax" -> {
                String apiKey = llmConfig.getMinimax().getApiKey();
                String model = customModelName != null ? customModelName : llmConfig.getMinimax().getModel();
                yield LLMFactory.createMiniMax(apiKey, model, httpClient);
            }
            case "glm" -> {
                String apiKey = llmConfig.getGlm().getApiKey();
                String model = customModelName != null ? customModelName : llmConfig.getGlm().getModel();
                yield LLMFactory.createZhipu(apiKey, model, httpClient);
            }
            case "openai" -> {
                String apiKey = llmConfig.getOpenai().getApiKey();
                String model = customModelName != null ? customModelName : llmConfig.getOpenai().getModel();
                yield LLMFactory.createOpenAI(apiKey, model, httpClient);
            }
            case "deepseek" -> {
                String apiKey = llmConfig.getDeepseek().getApiKey();
                String model = customModelName != null ? customModelName : llmConfig.getDeepseek().getModel();
                yield LLMFactory.createDeepSeek(apiKey, model, httpClient);
            }
            default -> throw new IllegalArgumentException("不支持的模型类型: " + modelType);
        };
    }
}
