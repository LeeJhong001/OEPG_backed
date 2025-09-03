package org.example.oepg.service.impl;

import org.example.oepg.dto.req.LoginRequest;
import org.example.oepg.dto.req.RegisterRequest;
import org.example.oepg.dto.res.AuthResponse;
import org.example.oepg.dto.res.UserInfoResponse;
import org.example.oepg.entity.User;
import org.example.oepg.exception.BusinessException;
import org.example.oepg.service.AuthService;
import org.example.oepg.service.UserService;
import org.example.oepg.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证服务实现类
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("=== 开始登录流程 ===");
        log.info("登录请求参数: username={}, password={}", loginRequest.getUsername(), "***");
        
        try {
            log.info("1. 开始验证用户名和密码...");
            
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            log.info("2. 用户名密码验证成功，获取用户详情...");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            log.info("3. 用户详情: username={}, authorities={}", userDetails.getUsername(), userDetails.getAuthorities());
            
            log.info("4. 生成JWT Token...");
            String token = jwtUtil.generateToken(userDetails);
            log.info("5. JWT Token生成成功，长度: {}", token.length());
            
            log.info("6. 获取用户完整信息...");
            User user = userService.findByUsername(loginRequest.getUsername());
            if (user == null) {
                log.error("用户验证成功但无法从数据库获取用户信息: {}", loginRequest.getUsername());
                throw new BusinessException("USER_NOT_FOUND", "用户信息获取失败");
            }
            log.info("7. 用户信息获取成功: id={}, role={}, status={}", user.getId(), user.getRole(), user.getStatus());
            
            UserInfoResponse userInfo = UserInfoResponse.fromUser(user);
            log.info("8. 用户信息转换完成");
            
            log.info("9. 登录成功，返回响应");
            return AuthResponse.loginSuccess(token, userInfo);
            
        } catch (Exception e) {
            log.error("登录失败，详细错误信息: ", e);
            log.error("错误类型: {}", e.getClass().getSimpleName());
            log.error("错误消息: {}", e.getMessage());
            
            // 重新抛出异常，让控制器处理
            throw new BusinessException("AUTHENTICATION_FAILED", "用户名或密码错误");
        }
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("=== 开始注册流程 ===");
        log.info("注册请求参数: username={}, email={}, role={}", 
                registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getRole());
        
        try {
            User user = userService.registerUser(registerRequest);
            log.info("用户注册成功: id={}, username={}", user.getId(), user.getUsername());
            
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities("ROLE_" + user.getRole().name())
                    .build();
            
            String token = jwtUtil.generateToken(userDetails);
            UserInfoResponse userInfo = UserInfoResponse.fromUser(user);
            
            log.info("注册成功，返回响应");
            return AuthResponse.registerSuccess(token, userInfo);
        } catch (Exception e) {
            log.error("注册失败，详细错误信息: ", e);
            throw new BusinessException("REGISTRATION_FAILED", e.getMessage());
        }
    }

    @Override
    public UserInfoResponse getCurrentUser(String username) {
        log.info("获取当前用户信息: username={}", username);
        
        User user = userService.findByUsername(username);
        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        log.info("用户信息获取成功: id={}, role={}", user.getId(), user.getRole());
        return UserInfoResponse.fromUser(user);
    }
} 