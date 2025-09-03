package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.oepg.dto.req.RegisterRequest;
import org.example.oepg.dto.req.UserCreateRequest;
import org.example.oepg.dto.req.UserQueryRequest;
import org.example.oepg.dto.req.UserUpdateRequest;
import org.example.oepg.dto.res.PageResponse;
import org.example.oepg.dto.res.UserInfoResponse;
import org.example.oepg.dto.res.UserListResponse;
import org.example.oepg.entity.User;
import org.example.oepg.enums.UserRole;
import org.example.oepg.exception.BusinessException;
import org.example.oepg.repository.UserRepository;
import org.example.oepg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            throw new BusinessException("USERNAME_EXISTS", "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "邮箱已存在");
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

    // ==================== 管理员用户管理功能实现 ====================

    @Override
    @Transactional
    public User createUser(UserCreateRequest createRequest) {
        // 检查用户名是否已存在
        if (existsByUsername(createRequest.getUsername())) {
            throw new BusinessException("USERNAME_EXISTS", "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (existsByEmail(createRequest.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "邮箱已存在");
        }

        // 创建新用户
        User user = User.builder()
                .username(createRequest.getUsername())
                .password(passwordEncoder.encode(createRequest.getPassword()))
                .realName(createRequest.getRealName())
                .email(createRequest.getEmail())
                .role(createRequest.getRole())
                .status(User.UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.insert(user);
        return user;
    }

    @Override
    public User findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        User user = userRepository.selectById(id);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateRequest updateRequest) {
        User existingUser = findById(id);

        // 检查用户名是否已被其他用户使用
        if (StringUtils.hasText(updateRequest.getUsername()) && 
            !updateRequest.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsernameAndNotId(updateRequest.getUsername(), id)) {
                throw new BusinessException("USERNAME_EXISTS", "用户名已被其他用户使用");
            }
            existingUser.setUsername(updateRequest.getUsername());
        }

        // 检查邮箱是否已被其他用户使用
        if (StringUtils.hasText(updateRequest.getEmail()) && 
            !updateRequest.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmailAndNotId(updateRequest.getEmail(), id)) {
                throw new BusinessException("EMAIL_EXISTS", "邮箱已被其他用户使用");
            }
            existingUser.setEmail(updateRequest.getEmail());
        }

        // 更新其他字段
        if (StringUtils.hasText(updateRequest.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        
        if (StringUtils.hasText(updateRequest.getRealName())) {
            existingUser.setRealName(updateRequest.getRealName());
        }
        
        if (updateRequest.getRole() != null) {
            existingUser.setRole(updateRequest.getRole());
        }
        
        if (updateRequest.getStatus() != null) {
            existingUser.setStatus(updateRequest.getStatus());
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        userRepository.updateById(existingUser);
        return existingUser;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        User user = findById(id);
        
        // 检查是否为系统管理员，如果是系统唯一的管理员则不能删除
        if (user.getRole().isAdmin()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role", user.getRole());
            long adminCount = userRepository.selectCount(queryWrapper);
            if (adminCount <= 1) {
                throw new BusinessException("CANNOT_DELETE_LAST_ADMIN", "无法删除系统唯一的管理员账户");
            }
        }
        
        return userRepository.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean batchDeleteUsers(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }

        // 检查是否包含管理员账户
        for (Long id : ids) {
            User user = userRepository.selectById(id);
            if (user != null && user.getRole().isAdmin()) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("role", user.getRole());
                long adminCount = userRepository.selectCount(queryWrapper);
                if (adminCount <= ids.length) {
                    throw new BusinessException("CANNOT_DELETE_ALL_ADMINS", "无法删除所有管理员账户，系统必须保留至少一个管理员");
                }
            }
        }

        return userRepository.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    @Override
    public IPage<User> getUserList(UserQueryRequest queryRequest) {
        Page<User> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());
        
        String roleStr = queryRequest.getRole() != null ? queryRequest.getRole().name() : null;
        String statusStr = queryRequest.getStatus() != null ? queryRequest.getStatus().name() : null;
        
        return userRepository.selectPageWithConditions(
                page,
                queryRequest.getUsername(),
                queryRequest.getRealName(), 
                queryRequest.getEmail(),
                roleStr,
                statusStr,
                queryRequest.getSortField(),
                queryRequest.getSortOrder()
        );
    }

    @Override
    @Transactional
    public boolean resetPassword(Long id, String newPassword) {
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码长度不能少于6位");
        }
        
        User user = findById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean toggleUserStatus(Long id) {
        User user = findById(id);
        
        // 如果是管理员且当前是激活状态，需要检查是否为最后一个管理员
        if (user.getRole().isAdmin() && user.getStatus() == User.UserStatus.ACTIVE) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role", user.getRole())
                      .eq("status", User.UserStatus.ACTIVE);
            long activeAdminCount = userRepository.selectCount(queryWrapper);
            if (activeAdminCount <= 1) {
                throw new BusinessException("CANNOT_DISABLE_LAST_ADMIN", "无法禁用系统唯一的激活管理员账户");
            }
        }
        
        // 切换状态
        User.UserStatus newStatus = user.getStatus() == User.UserStatus.ACTIVE 
                ? User.UserStatus.INACTIVE 
                : User.UserStatus.ACTIVE;
        
        user.setStatus(newStatus);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.updateById(user) > 0;
    }

    // ==================== 带响应格式的管理员功能实现 ====================

    @Override
    public Map<String, Object> getUserListWithResponse(UserQueryRequest queryRequest) {
        try {
            IPage<User> userPage = getUserList(queryRequest);
            
            // 转换为响应DTO
            List<UserListResponse> userList = userPage.getRecords().stream()
                    .map(UserListResponse::fromUser)
                    .collect(Collectors.toList());
            
            PageResponse<UserListResponse> response = new PageResponse<>();
            response.setRecords(userList);
            response.setTotal(userPage.getTotal());
            response.setSize(userPage.getSize());
            response.setCurrent(userPage.getCurrent());
            response.setPages(userPage.getPages());
            response.setHasPrevious(userPage.getCurrent() > 1);
            response.setHasNext(userPage.getCurrent() < userPage.getPages());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取用户列表失败：" + e.getMessage());
            result.put("data", PageResponse.empty());
            return result;
        }
    }

    @Override
    public Map<String, Object> getUserByIdWithResponse(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = findById(id);
            UserInfoResponse userInfo = UserInfoResponse.fromUser(user);
            response.put("success", true);
            response.put("data", userInfo);
            return response;
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户信息失败，请稍后重试");
            return response;
        }
    }

    @Override
    public Map<String, Object> createUserWithResponse(UserCreateRequest createRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = createUser(createRequest);
            response.put("success", true);
            response.put("message", "用户创建成功");
            response.put("data", UserInfoResponse.fromUser(user));
            return response;
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建用户失败，请稍后重试");
            return response;
        }
    }

    @Override
    public Map<String, Object> updateUserWithResponse(Long id, UserUpdateRequest updateRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = updateUser(id, updateRequest);
            response.put("success", true);
            response.put("message", "用户信息更新成功");
            response.put("data", UserInfoResponse.fromUser(user));
            return response;
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新用户信息失败，请稍后重试");
            return response;
        }
    }

    @Override
    public Map<String, Object> deleteUserWithResponse(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = deleteUser(id);
            if (success) {
                response.put("success", true);
                response.put("message", "用户删除成功");
            } else {
                response.put("success", false);
                response.put("message", "用户删除失败");
            }
            return response;
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除用户失败，请稍后重试");
            return response;
        }
    }

    @Override
    public Map<String, Object> batchDeleteUsersWithResponse(Long[] ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (ids == null || ids.length == 0) {
                response.put("success", false);
                response.put("message", "用户ID列表不能为空");
                return response;
            }
            
            boolean success = batchDeleteUsers(ids);
            if (success) {
                response.put("success", true);
                response.put("message", "批量删除用户成功");
            } else {
                response.put("success", false);
                response.put("message", "批量删除用户失败");
            }
            return response;
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量删除用户失败，请稍后重试");
            return response;
        }
    }

    @Override
    public Map<String, Object> resetPasswordWithResponse(Long id, String newPassword) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "新密码不能为空");
                return response;
            }
            
            boolean success = resetPassword(id, newPassword);
            if (success) {
                response.put("success", true);
                response.put("message", "密码重置成功");
            } else {
                response.put("success", false);
                response.put("message", "密码重置失败");
            }
            return response;
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "重置密码失败，请稍后重试");
            return response;
        }
    }

    @Override
    public Map<String, Object> toggleUserStatusWithResponse(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = toggleUserStatus(id);
            if (success) {
                User user = findById(id);
                response.put("success", true);
                response.put("message", "用户状态切换成功");
                response.put("data", UserInfoResponse.fromUser(user));
            } else {
                response.put("success", false);
                response.put("message", "用户状态切换失败");
            }
            return response;
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "切换用户状态失败，请稍后重试");
            return response;
        }
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        try {
            // 简化实现，先获取总用户数
            QueryWrapper<User> totalQueryWrapper = new QueryWrapper<>();
            long totalUsers = userRepository.selectCount(totalQueryWrapper);
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", totalUsers);
            
            // 暂时返回基础统计信息，避免枚举查询问题
            try {
                // 使用selectList获取所有用户数据进行统计，适用于用户数量不大的情况
                List<User> allUsers = userRepository.selectList(new QueryWrapper<>());
                
                long studentCount = allUsers.stream().filter(u -> u.getRole() == UserRole.STUDENT).count();
                long teacherCount = allUsers.stream().filter(u -> u.getRole() == UserRole.TEACHER).count();
                long adminCount = allUsers.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();
                long activeCount = allUsers.stream().filter(u -> u.getStatus() == User.UserStatus.ACTIVE).count();
                long inactiveCount = allUsers.stream().filter(u -> u.getStatus() == User.UserStatus.INACTIVE).count();
                
                statistics.put("studentCount", studentCount);
                statistics.put("teacherCount", teacherCount);
                statistics.put("adminCount", adminCount);
                statistics.put("activeCount", activeCount);
                statistics.put("inactiveCount", inactiveCount);
            } catch (Exception e) {
                // 如果详细统计失败，返回基础信息
                statistics.put("studentCount", 0);
                statistics.put("teacherCount", 0);
                statistics.put("adminCount", 0);
                statistics.put("activeCount", 0);
                statistics.put("inactiveCount", 0);
            }
            
            return statistics;
        } catch (Exception e) {
            // 出错时返回默认值
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", 0);
            statistics.put("studentCount", 0);
            statistics.put("teacherCount", 0);
            statistics.put("adminCount", 0);
            statistics.put("activeCount", 0);
            statistics.put("inactiveCount", 0);
            return statistics;
        }
    }
} 