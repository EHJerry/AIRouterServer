package com.aitools.llm;

import java.util.List;

/**
 * LLM客户端接口
 */
public interface LLMClient {
    
    /**
     * 简单对话
     * @param message 用户消息
     * @return 回复内容
     */
    default String chat(String message) {
        return chat(new Message("user", message));
    }
    
    /**
     * 单条消息对话
     * @param message 消息
     * @return 回复内容
     */
    default String chat(Message message) {
        List<Message> messages = new java.util.ArrayList<>();
        messages.add(message);
        return chat(messages).getContent();
    }
    
    /**
     * 多消息对话，返回完整响应
     * @param messages 消息列表
     * @return 响应对象
     */
    LLMResponse chat(List<Message> messages);
}
