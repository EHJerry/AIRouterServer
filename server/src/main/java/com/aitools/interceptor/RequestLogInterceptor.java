package com.aitools.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

/**
 * 请求日志拦截器
 */
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = getClientIp(request);

        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n======================================= REQUEST START =======================================");
        logBuilder.append("\n[").append(LocalDateTime.now().format(FORMATTER)).append("]");
        logBuilder.append("\nMethod: ").append(method);
        logBuilder.append("\nURI: ").append(uri);
        if (queryString != null && !queryString.isEmpty()) {
            logBuilder.append("?").append(queryString);
        }
        logBuilder.append("\nClient IP: ").append(clientIp);
        logBuilder.append("\nHeaders:");
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // 敏感信息不打印
            if (!headerName.toLowerCase().contains("authorization") && 
                !headerName.toLowerCase().contains("cookie")) {
                logBuilder.append("\n  - ").append(headerName).append(": ").append(headerValue);
            }
        }
        logBuilder.append("\n---------------------------------------------------------------------------------------------");

        logger.info(logBuilder.toString());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int statusCode = response.getStatus();

        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("---------------------------------------------------------------------------------------------");
        logBuilder.append("\nResponse Status: ").append(statusCode);
        logBuilder.append("\nDuration: ").append(duration).append("ms");
        logBuilder.append("\n[").append(LocalDateTime.now().format(FORMATTER)).append("]");
        logBuilder.append("\n======================================= REQUEST END =======================================\n");

        if (ex != null) {
            logger.error("Request [{} {}] failed with exception: {}", method, uri, ex.getMessage(), ex);
        } else {
            logger.info(logBuilder.toString());
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
