package com.aitools.controller;

import com.aitools.dto.ChatRequest;
import com.aitools.dto.ChatResponse;
import com.aitools.service.LLmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * LLM代理控制器 V1版本
 */
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class V1Controller {
    
    private static final Logger logger = LoggerFactory.getLogger(V1Controller.class);
    
    private final LLmService llmService;
    
    public V1Controller(LLmService llmService) {
        this.llmService = llmService;
    }
    
    /**
     * 异步聊天接口（支持高并发）
     * POST /v1/chat
     * 
     * 请求示例:
     * {
     *   "modelType": "minimax",
     *   "modelName": "MiniMax-M3",
     *   "question": "你好，请介绍一下你自己"
     * }
     * 
     * 响应示例:
     * {
     *   "success": true,
     *   "answer": "我是MiniMax大模型...",
     *   "model": "MiniMax-M3",
     *   "tokens": 42
     * }
     */
    @PostMapping("/chat")
    public CompletableFuture<ResponseEntity<ChatResponse>> chat(@RequestBody ChatRequest request) {
        logger.info("[V1接口] 收到聊天请求 - modelType: {}, modelName: {}, question: {}", 
                request.getModelType(), request.getModelName(), request.getQuestion());
        
        return llmService.chatAsync(request)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        logger.info("[V1接口] 请求成功 - model: {}, tokens: {}", 
                                response.getModel(), response.getTokens());
                    } else {
                        logger.error("[V1接口] 请求失败 - error: {}", response.getError());
                    }
                    return ResponseEntity.ok(response);
                })
                .exceptionally(e -> {
                    logger.error("[V1接口] 请求异常 - error: {}", e.getMessage());
                    return ResponseEntity.ok(ChatResponse.error("请求处理异常: " + e.getMessage()));
                });
    }
}
