package com.aitools.controller;

import com.aitools.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * 
 * 密码处理说明：
 * 前端已对密码进行 SHA-256(password + 秘钥) 加密
 * 后端直接存储/验证密文密码，不再进行二次加密
 * 
 * 加密流程：用户输入密码 → SHA-256(password + 秘钥) → 传输密文 → 后端直接存储/验证
 */
@RestController
@RequestMapping("/api")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 用户登录接口
     * POST /api/login
     * 
     * 请求体: {"username": "...", "password": "SHA-256哈希值"}
     * 
     * 响应: {"success": true, "message": "...", "user": {...}}
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        logger.info("[登录接口] 收到登录请求 - username: {}", username);
        
        Map<String, Object> response = new HashMap<>();
        
        if (userService.login(username, password)) {
            response.put("success", true);
            response.put("message", "登录成功");
            
            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("nickname", userService.getNickname(username));
            user.put("avatar", userService.getAvatar(username));
            response.put("user", user);
            
            logger.info("[登录接口] 登录成功 - username: {}", username);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            logger.warn("[登录接口] 登录失败 - username: {}", username);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户注册接口
     * POST /api/register
     * 
     * 请求体: {"username": "...", "password": "SHA-256哈希值"}
     * 
     * 响应: {"success": true, "message": "...", "user": {...}}
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        logger.info("[注册接口] 收到注册请求 - username: {}", username);
        
        Map<String, Object> response = new HashMap<>();
        
        // 验证用户名格式
        if (!validateUsername(username)) {
            response.put("success", false);
            response.put("message", "用户名格式不正确，只能包含字母、数字和下划线，长度3-20位");
            return ResponseEntity.ok(response);
        }
        
        // 验证密码格式（SHA-256应为64位十六进制）
        if (!validatePassword(password)) {
            response.put("success", false);
            response.put("message", "密码格式不正确");
            return ResponseEntity.ok(response);
        }
        
        if (userService.register(username, password)) {
            response.put("success", true);
            response.put("message", "注册成功");
            
            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("nickname", username); // 默认昵称等于用户名
            user.put("avatar", null);
            response.put("user", user);
            
            logger.info("[注册接口] 注册成功 - username: {}", username);
        } else {
            response.put("success", false);
            response.put("message", "用户名已存在");
            logger.warn("[注册接口] 注册失败 - username: {} 已存在", username);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户名查重接口
     * GET /api/check-username?username={username}
     * 
     * 响应: {"available": true/false}
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam("username") String username) {
        logger.info("[用户名查重] 检查用户名 - username: {}", username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("available", !userService.existsByUsername(username));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 验证用户名格式
     * 只能包含字母、数字和下划线，长度3-20位
     */
    private boolean validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    /**
     * 验证密码格式
     * SHA-256哈希值应为64位十六进制字符串
     */
    private boolean validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.matches("^[a-f0-9]{64}$");
    }
}