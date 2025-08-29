package org.example.oepg.controller;

import org.example.oepg.dto.req.LoginRequest;
import org.example.oepg.dto.req.RegisterRequest;
import org.example.oepg.dto.res.AuthResponse;
import org.example.oepg.dto.res.UserInfoResponse;
import org.example.oepg.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关请求
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(AuthResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(AuthResponse.error("登录失败，请稍后重试"));
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(AuthResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(AuthResponse.error("注册失败，请稍后重试"));
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            String username = authentication.getName();
            UserInfoResponse userInfo = authService.getCurrentUser(username);
            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

} 