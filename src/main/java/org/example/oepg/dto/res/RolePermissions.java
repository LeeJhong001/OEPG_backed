package org.example.oepg.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色权限信息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissions {
    private String code;
    private String displayName;
    private boolean isStudent;
    private boolean isTeacher;
    private boolean isAdmin;
    private boolean hasTeacherOrAdminPermission;
    private boolean hasAdminPermission;
} 