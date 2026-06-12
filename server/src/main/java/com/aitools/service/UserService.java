package com.aitools.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务
 * 用户数据存储在项目根目录的 users.txt 文件中
 * 
 * 密码处理说明：
 * 前端已对密码进行 SHA-256(password + 秘钥) 加密
 * 后端直接存储密文密码，不再进行二次加密
 * 登录时直接对比前端传来的密文密码与存储的密文密码
 * 
 * 加密流程：用户输入密码 → SHA-256(password + 秘钥) → 传输密文 → 后端直接存储/验证
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    // 用户数据存储 Map<username, UserData>
    // 使用 ConcurrentHashMap 保证并发读写安全
    private Map<String, UserData> users = new ConcurrentHashMap<>();
    
    // 用户数据文件路径（相对于项目根目录的 server/users.txt）
    // 使用系统属性确保路径在不同工作目录下运行时保持一致
    private static final String USER_FILE_PATH;
    
    static {
        String userDir = System.getProperty("user.dir");
        File serverDir = new File(userDir, "server");
        if (serverDir.exists() && serverDir.isDirectory()) {
            // 工作目录是项目根目录，文件在 server 目录下
            USER_FILE_PATH = new File(serverDir, "users.txt").getAbsolutePath();
        } else {
            // 工作目录是 server 模块目录
            USER_FILE_PATH = new File(userDir, "users.txt").getAbsolutePath();
        }
        System.out.println("[UserService] 用户数据文件路径: " + USER_FILE_PATH);
    }
    
    // 保存操作的同步锁，防止并发写入导致文件损坏
    private final Object saveLock = new Object();
    
    /**
     * 初始化时加载用户数据
     */
    @PostConstruct
    public void init() {
        loadUsers();
    }
    
    /**
     * 从文件加载用户数据
     */
    private void loadUsers() {
        File file = new File(USER_FILE_PATH);
        if (!file.exists()) {
            logger.info("用户数据文件不存在，将创建新文件");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                // 格式: username:password:nickname:avatar
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String nickname = parts.length > 2 ? parts[2].trim() : username;
                    String avatar = parts.length > 3 ? parts[3].trim() : null;
                    
                    if (avatar != null && avatar.equals("null")) {
                        avatar = null;
                    }
                    
                    users.put(username, new UserData(username, password, nickname, avatar));
                }
            }
            logger.info("成功加载 {} 个用户数据", users.size());
        } catch (IOException e) {
            logger.error("加载用户数据失败", e);
        }
    }
    
    /**
     * 保存用户数据到文件
     * 使用同步锁保证线程安全，防止多个用户同时注册时并发写入导致文件损坏
     */
    private void saveUsers() {
        synchronized (saveLock) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(USER_FILE_PATH), StandardCharsets.UTF_8))) {
                for (UserData user : users.values()) {
                    writer.write(String.format("%s:%s:%s:%s", 
                            user.username, 
                            user.password, 
                            user.nickname, 
                            user.avatar != null ? user.avatar : "null"));
                    writer.newLine();
                }
                logger.info("成功保存 {} 个用户数据", users.size());
            } catch (IOException e) {
                logger.error("保存用户数据失败", e);
            }
        }
    }
    
    /**
     * 用户登录
     * 直接对比前端传来的密文密码与存储的密文密码
     * 前端已进行 SHA-256(password + 秘钥) 加密，后端不再加密
     * 
     * @param username 用户名
     * @param password 密文密码（前端已加密）
     * @return 是否登录成功
     */
    public boolean login(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return false;
        }
        
        UserData user = users.get(username);
        return user != null && user.password.equals(password);
    }
    
    /**
     * 用户注册
     * 直接存储前端传来的密文密码
     * 前端已进行 SHA-256(password + 秘钥) 加密，后端不再加密
     * 
     * @param username 用户名
     * @param password 密文密码（前端已加密）
     * @return 是否注册成功
     */
    public boolean register(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return false;
        }
        
        if (users.containsKey(username)) {
            return false;
        }
        
        users.put(username, new UserData(username, password, username, null));
        saveUsers();
        return true;
    }
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return true; // 空用户名视为已存在（不允许注册）
        }
        return users.containsKey(username);
    }
    
    /**
     * 获取用户昵称
     * @param username 用户名
     * @return 昵称
     */
    public String getNickname(String username) {
        UserData user = users.get(username);
        return user != null ? user.nickname : username;
    }
    
    /**
     * 获取用户头像
     * @param username 用户名
     * @return 头像URL
     */
    public String getAvatar(String username) {
        UserData user = users.get(username);
        return user != null ? user.avatar : null;
    }
    
    /**
     * 用户数据内部类
     */
    private static class UserData {
        String username;
        String password;
        String nickname;
        String avatar;
        
        UserData(String username, String password, String nickname, String avatar) {
            this.username = username;
            this.password = password;
            this.nickname = nickname;
            this.avatar = avatar;
        }
    }
}