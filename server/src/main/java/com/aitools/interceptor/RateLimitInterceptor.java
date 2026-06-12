package com.aitools.interceptor;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求限流拦截器
 * 使用Guava RateLimiter实现简单的限流功能
 * 
 * 限流规则：
 * - 登录接口：5次/秒
 * - 注册接口：3次/秒
 * - 聊天接口：10次/秒
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);
    
    private static final Gson gson = new Gson();
    
    // 登录接口限流：5次/秒
    private final RateLimiter loginLimiter = RateLimiter.create(5.0);
    
    // 注册接口限流：3次/秒
    private final RateLimiter registerLimiter = RateLimiter.create(3.0);
    
    // 聊天接口限流：10次/秒
    private final RateLimiter chatLimiter = RateLimiter.create(10.0);
    
    // 通用接口限流：20次/秒
    private final RateLimiter generalLimiter = RateLimiter.create(20.0);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        
        RateLimiter limiter = getLimiter(requestUri);
        
        if (!limiter.tryAcquire()) {
            logger.warn("[限流拦截] 请求被拒绝 - URI: {}, Method: {}", requestUri, method);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(429);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "请求过于频繁，请稍后再试");
            
            // 使用 try-with-resources 自动关闭资源，避免资源泄漏
            try (PrintWriter writer = response.getWriter()) {
                writer.write(gson.toJson(result));
                writer.flush();
            }
            
            return false;
        }
        
        return true;
    }
    
    /**
     * 根据请求URI获取对应的限流控制器
     */
    private RateLimiter getLimiter(String requestUri) {
        if (requestUri.contains("/api/login")) {
            return loginLimiter;
        } else if (requestUri.contains("/api/register")) {
            return registerLimiter;
        } else if (requestUri.contains("/v1/chat")) {
            return chatLimiter;
        } else {
            return generalLimiter;
        }
    }
}