package org.example.oepg.controller;

import org.example.oepg.dto.req.UserCreateRequest;
import org.example.oepg.dto.req.UserQueryRequest;
import org.example.oepg.dto.req.UserUpdateRequest;
import org.example.oepg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 用户管理控制器
 * 管理员对用户的增删改查功能
 */
@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public ResponseEntity<Object> getUserList(UserQueryRequest queryRequest) {
        Map<String, Object> result = userService.getUserListWithResponse(queryRequest);
        boolean success = (boolean) result.get("success");
        
        if (success) {
            return ResponseEntity.ok(result.get("data"));
        } else {
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        Map<String, Object> result = userService.getUserByIdWithResponse(id);
        boolean success = (boolean) result.get("success");
        
        if (success) {
            return ResponseEntity.ok(result.get("data"));
        } else {
            String message = (String) result.get("message");
            if (message.contains("用户不存在")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        }
    }

    /**
     * 创建新用户
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserCreateRequest createRequest) {
        Map<String, Object> result = userService.createUserWithResponse(createRequest);
        boolean success = (boolean) result.get("success");
        
        if (success) {
            return ResponseEntity.ok(result);
        } else {
            String message = (String) result.get("message");
            if (message.contains("已存在") || message.contains("不能为空") || message.contains("格式")) {
                return ResponseEntity.badRequest().body(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, 
                                                          @Valid @RequestBody UserUpdateRequest updateRequest) {
        Map<String, Object> result = userService.updateUserWithResponse(id, updateRequest);
        boolean success = (boolean) result.get("success");
        
        if (success) {
            return ResponseEntity.ok(result);
        } else {
            String message = (String) result.get("message");
            if (message.contains("不存在") || message.contains("已被") || message.contains("不能为空")) {
                return ResponseEntity.badRequest().body(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = userService.deleteUserWithResponse(id);
        boolean success = (boolean) result.get("success");
        
        if (success) {
            return ResponseEntity.ok(result);
        } else {
            String message = (String) result.get("message");
            if (message.contains("无法删除") || message.contains("不存在")) {
                return ResponseEntity.badRequest().body(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteUsers(@RequestBody Map<String, Long[]> requestBody) {
        Long[] ids = requestBody.get("ids");
        Map<String, Object> result = userService.batchDeleteUsersWithResponse(ids);
        boolean success = (boolean) result.get("success");
        
        if (success) {
            return ResponseEntity.ok(result);
        } else {
            String message = (String) result.get("message");
            if (message.contains("不能为空") || message.contains("无法删除")) {
                return ResponseEntity.badRequest().body(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id, 
                                                            @RequestBody Map<String, String> requestBody) {
        String newPassword = requestBody.get("newPassword");
        Map<String, Object> result = userService.resetPasswordWithResponse(id, newPassword);
        boolean success = (boolean) result.get("success");
        
        if (success) {
            return ResponseEntity.ok(result);
        } else {
            String message = (String) result.get("message");
            if (message.contains("不能为空") || message.contains("长度") || message.contains("不存在")) {
                return ResponseEntity.badRequest().body(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        }
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Long id) {
        Map<String, Object> result = userService.toggleUserStatusWithResponse(id);
        boolean success = (boolean) result.get("success");
        
        if (success) {
            return ResponseEntity.ok(result);
        } else {
            String message = (String) result.get("message");
            if (message.contains("无法禁用") || message.contains("不存在")) {
                return ResponseEntity.badRequest().body(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> statistics = userService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }
}
