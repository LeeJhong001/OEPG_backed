package org.example.oepg.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.oepg.dto.req.RegisterRequest;
import org.example.oepg.dto.req.UserCreateRequest;
import org.example.oepg.dto.req.UserQueryRequest;
import org.example.oepg.dto.req.UserUpdateRequest;
import org.example.oepg.entity.User;

import java.util.Map;

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
    
    // ==================== 管理员用户管理功能 ====================
    
    /**
     * 创建用户（管理员功能）
     */
    User createUser(UserCreateRequest createRequest);
    
    /**
     * 根据ID查找用户
     */
    User findById(Long id);
    
    /**
     * 更新用户信息（管理员功能）
     */
    User updateUser(Long id, UserUpdateRequest updateRequest);
    
    /**
     * 删除用户（管理员功能）
     */
    boolean deleteUser(Long id);
    
    /**
     * 批量删除用户（管理员功能）
     */
    boolean batchDeleteUsers(Long[] ids);
    
    /**
     * 分页查询用户列表（管理员功能）
     */
    IPage<User> getUserList(UserQueryRequest queryRequest);
    
    /**
     * 分页查询用户列表并返回DTO（管理员功能）
     */
    Map<String, Object> getUserListWithResponse(UserQueryRequest queryRequest);
    
    /**
     * 根据ID获取用户详情并返回DTO（管理员功能）
     */
    Map<String, Object> getUserByIdWithResponse(Long id);
    
    /**
     * 创建用户并返回响应（管理员功能）
     */
    Map<String, Object> createUserWithResponse(UserCreateRequest createRequest);
    
    /**
     * 更新用户并返回响应（管理员功能）
     */
    Map<String, Object> updateUserWithResponse(Long id, UserUpdateRequest updateRequest);
    
    /**
     * 删除用户并返回响应（管理员功能）
     */
    Map<String, Object> deleteUserWithResponse(Long id);
    
    /**
     * 批量删除用户并返回响应（管理员功能）
     */
    Map<String, Object> batchDeleteUsersWithResponse(Long[] ids);
    
    /**
     * 重置用户密码（管理员功能）
     */
    boolean resetPassword(Long id, String newPassword);
    
    /**
     * 重置用户密码并返回响应（管理员功能）
     */
    Map<String, Object> resetPasswordWithResponse(Long id, String newPassword);
    
    /**
     * 启用/禁用用户（管理员功能）
     */
    boolean toggleUserStatus(Long id);
    
    /**
     * 启用/禁用用户并返回响应（管理员功能）
     */
    Map<String, Object> toggleUserStatusWithResponse(Long id);
    
    /**
     * 获取用户统计信息（管理员功能）
     */
    Map<String, Object> getUserStatistics();
} 