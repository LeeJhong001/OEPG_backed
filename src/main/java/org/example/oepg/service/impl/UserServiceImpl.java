package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.oepg.dto.req.RegisterRequest;
import org.example.oepg.entity.User;
import org.example.oepg.repository.UserRepository;
import org.example.oepg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 创建新用户
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .realName(registerRequest.getRealName())
                .email(registerRequest.getEmail())
                .role(registerRequest.getRole())
                .status(User.UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.insert(user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userRepository.selectOne(queryWrapper);
    }

    @Override
    public boolean existsByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userRepository.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userRepository.selectCount(queryWrapper) > 0;
    }
} 