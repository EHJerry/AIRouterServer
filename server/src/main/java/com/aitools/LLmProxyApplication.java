package com.aitools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LLM代理服务端启动类
 */
@SpringBootApplication
public class LLmProxyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LLmProxyApplication.class, args);
    }
}
