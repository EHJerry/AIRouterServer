package com.aitools.controller;

import com.aitools.dto.ChatRequest;
import com.aitools.dto.ChatResponse;
import com.aitools.service.LLmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * LLM代理控制器
 */
@RestController
@RequestMapping("/api/llm")
@CrossOrigin(origins = "*")
public class LLmController {
    
    private static final Logger logger = LoggerFactory.getLogger(LLmController.class);
    
    private final LLmService llmService;
    
    public LLmController(LLmService llmService) {
        this.llmService = llmService;
    }
    
    /**
     * 聊天接口 (旧版本)
     * POST /api/llm/chat
     * 
     * 请求示例:
     * {
     *   "modelType": "minimax",
     *   "modelName": "MiniMax-M3",
     *   "question": "你好，请介绍一下你自己"
     * }
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        logger.info("收到聊天请求 - modelType: {}, modelName: {}, question: {}", 
                request.getModelType(), request.getModelName(), request.getQuestion());
        
        long startTime = System.currentTimeMillis();
        ChatResponse response = llmService.chat(request);
        long duration = System.currentTimeMillis() - startTime;
        
        if (response.isSuccess()) {
            logger.info("请求成功 - model: {}, tokens: {}, duration: {}ms", 
                    response.getModel(), response.getTokens(), duration);
        } else {
            logger.error("请求失败 - error: {}, duration: {}ms", 
                    response.getError(), duration);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
