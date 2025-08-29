package org.example.oepg.service;

import org.example.oepg.dto.req.RegisterRequest;
import org.example.oepg.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 注册新用户
     */
    User registerUser(RegisterRequest registerRequest);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
} 