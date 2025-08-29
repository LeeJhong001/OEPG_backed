package org.example.oepg.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn = 86400000L; // 24小时，单位：毫秒
    private String message;
    private UserInfoResponse userInfo;
    
    /**
     * 创建登录成功响应
     */
    public static AuthResponse loginSuccess(String token, UserInfoResponse userInfo) {
        return new AuthResponse(token, "Bearer", 86400000L, "登录成功", userInfo);
    }
    
    /**
     * 创建注册成功响应
     */
    public static AuthResponse registerSuccess(String token, UserInfoResponse userInfo) {
        return new AuthResponse(token, "Bearer", 86400000L, "注册成功", userInfo);
    }
    
    /**
     * 创建错误响应
     */
    public static AuthResponse error(String message) {
        return new AuthResponse(null, null, null, message, null);
    }
} 