package com.aitools.llm;

import com.aitools.llm.impl.DeepSeekClient;
import com.aitools.llm.impl.MiniMaxClient;
import com.aitools.llm.impl.OpenAIClient;
import com.aitools.llm.impl.ZhipuClient;

import java.net.http.HttpClient;

/**
 * LLM客户端工厂
 */
public class LLMFactory {
    
    /**
     * 创建MiniMax客户端
     */
    public static LLMClient createMiniMax(String apiKey) {
        return new MiniMaxClient(apiKey);
    }
    
    public static LLMClient createMiniMax(String apiKey, String model) {
        return new MiniMaxClient(apiKey, model);
    }
    
    public static LLMClient createMiniMax(String apiKey, String model, HttpClient httpClient) {
        return new MiniMaxClient(apiKey, model, httpClient);
    }
    
    /**
     * 创建智谱GLM客户端
     */
    public static LLMClient createZhipu(String apiKey) {
        return new ZhipuClient(apiKey);
    }
    
    public static LLMClient createZhipu(String apiKey, String model) {
        return new ZhipuClient(apiKey, model);
    }
    
    public static LLMClient createZhipu(String apiKey, String model, HttpClient httpClient) {
        return new ZhipuClient(apiKey, model, httpClient);
    }
    
    /**
     * 创建OpenAI客户端
     */
    public static LLMClient createOpenAI(String apiKey) {
        return new OpenAIClient(apiKey);
    }
    
    public static LLMClient createOpenAI(String apiKey, String model) {
        return new OpenAIClient(apiKey, model);
    }
    
    public static LLMClient createOpenAI(String apiKey, String model, HttpClient httpClient) {
        return new OpenAIClient(apiKey, model, httpClient);
    }
    
    /**
     * 创建DeepSeek客户端
     */
    public static LLMClient createDeepSeek(String apiKey) {
        return new DeepSeekClient(apiKey);
    }
    
    public static LLMClient createDeepSeek(String apiKey, String model) {
        return new DeepSeekClient(apiKey, model);
    }
    
    public static LLMClient createDeepSeek(String apiKey, String model, HttpClient httpClient) {
        return new DeepSeekClient(apiKey, model, httpClient);
    }
    
    /**
     * 根据类型创建客户端
     */
    public static LLMClient create(String modelType, String apiKey, String modelName) {
        return switch (modelType.toLowerCase()) {
            case "minimax" -> createMiniMax(apiKey, modelName);
            case "glm", "zhipu" -> createZhipu(apiKey, modelName);
            case "openai" -> createOpenAI(apiKey, modelName);
            case "deepseek" -> createDeepSeek(apiKey, modelName);
            default -> throw new IllegalArgumentException("不支持的模型类型: " + modelType);
        };
    }
    
    /**
     * 根据类型创建客户端（带HttpClient）
     */
    public static LLMClient create(String modelType, String apiKey, String modelName, HttpClient httpClient) {
        return switch (modelType.toLowerCase()) {
            case "minimax" -> createMiniMax(apiKey, modelName, httpClient);
            case "glm", "zhipu" -> createZhipu(apiKey, modelName, httpClient);
            case "openai" -> createOpenAI(apiKey, modelName, httpClient);
            case "deepseek" -> createDeepSeek(apiKey, modelName, httpClient);
            default -> throw new IllegalArgumentException("不支持的模型类型: " + modelType);
        };
    }
}
