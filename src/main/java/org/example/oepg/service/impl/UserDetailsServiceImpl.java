package org.example.oepg.service.impl;

import org.example.oepg.entity.User;
import org.example.oepg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

/**
 * UserDetailsService 实现类
 * 用于 Spring Security 的用户认证
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("=== UserDetailsService.loadUserByUsername 被调用 ===");
        log.info("查找用户名: {}", username);
        
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                log.error("用户不存在: {}", username);
                throw new UsernameNotFoundException("用户不存在: " + username);
            }
            
            log.info("用户查找成功: id={}, username={}, role={}, status={}", 
                    user.getId(), user.getUsername(), user.getRole(), user.getStatus());
            
            // 检查用户状态
            if (user.getStatus() != User.UserStatus.ACTIVE) {
                log.warn("用户状态非激活: username={}, status={}", username, user.getStatus());
            }
            
            // 构建UserDetails
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                    .disabled(user.getStatus() != User.UserStatus.ACTIVE)
                    .accountExpired(false)
                    .credentialsExpired(false)
                    .accountLocked(false)
                    .build();
            
            log.info("UserDetails构建成功: username={}, authorities={}, enabled={}", 
                    userDetails.getUsername(), userDetails.getAuthorities(), userDetails.isEnabled());
            
            return userDetails;
            
        } catch (Exception e) {
            log.error("loadUserByUsername发生异常: ", e);
            throw e;
        }
    }
} 