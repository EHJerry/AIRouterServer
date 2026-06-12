package com.aitools.config;

import com.aitools.interceptor.RateLimitInterceptor;
import com.aitools.interceptor.RequestLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Web MVC配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestLogInterceptor requestLogInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    public WebConfig(RequestLogInterceptor requestLogInterceptor, RateLimitInterceptor rateLimitInterceptor) {
        this.requestLogInterceptor = requestLogInterceptor;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册请求限流拦截器，拦截登录、注册和聊天接口
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/login", "/api/register", "/v1/chat");
        
        // 注册请求日志拦截器，拦截所有请求
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/llm/health");
    }

    /**
     * CORS跨域配置
     * 允许前端域名跨域访问
     * 
     * 安全注意：当设置allowCredentials=true时，不能使用通配符*作为允许的源
     * 必须指定具体的域名，否则存在CSRF风险
     * 
     * @Order(Ordered.HIGHEST_PRECEDENCE) 确保CORS过滤器最先执行，在拦截器之前处理预检请求
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的源（前端域名）- 指定具体域名，避免CSRF风险
        // 开发环境常见的前端端口：3000(React)、5173-5176(Vite)、8080(通用)、8090(后端)
        config.setAllowedOriginPatterns(Arrays.asList(
                // HTTP协议
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175",
                "http://localhost:5176",
                "http://localhost:8080",
                "http://localhost:8090",
                // HTTPS协议
                "https://localhost:3000",
                "https://localhost:5173",
                "https://localhost:5174",
                "https://localhost:5175",
                "https://localhost:5176",
                "https://localhost:8080",
                "https://localhost:8090"
        ));
        
        // 允许的请求方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许的请求头
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // 是否允许发送Cookie
        config.setAllowCredentials(true);
        
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
