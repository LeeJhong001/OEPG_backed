package org.example.oepg.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.oepg.entity.User;
import org.example.oepg.enums.UserRole;

import java.time.LocalDateTime;

/**
 * 用户信息响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    
    private Long id;
    private String username;
    private String realName;
    private String email;
    private UserRole role;
    private String roleDisplayName;
    private User.UserStatus status;
    private String statusDisplayName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 从 User 实体创建 UserInfoResponse
     */
    public static UserInfoResponse fromUser(User user) {
        if (user == null) {
            return null;
        }
        
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setRoleDisplayName(user.getRole() != null ? user.getRole().getDisplayName() : null);
        response.setStatus(user.getStatus());
        response.setStatusDisplayName(user.getStatus() != null ? user.getStatus().getDisplayName() : null);
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        return response;
    }
} 