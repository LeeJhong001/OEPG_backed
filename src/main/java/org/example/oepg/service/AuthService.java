package org.example.oepg.service;

import org.example.oepg.dto.req.LoginRequest;
import org.example.oepg.dto.req.RegisterRequest;
import org.example.oepg.dto.res.AuthResponse;
import org.example.oepg.dto.res.UserInfoResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest loginRequest);
    
    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest registerRequest);
    
    /**
     * 获取当前用户信息
     */
    UserInfoResponse getCurrentUser(String username);
} 