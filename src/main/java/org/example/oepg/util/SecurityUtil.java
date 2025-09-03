package org.example.oepg.util;


import org.example.oepg.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 提供获取当前用户信息和检查权限的方法
 */
public class SecurityUtil {

    /**
     * 获取当前认证信息
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前用户角色
     */
    public static UserRole getCurrentUserRole() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            for (Object authority : authentication.getAuthorities()) {
                String roleName = authority.toString();
                if (roleName.startsWith("ROLE_")) {
                    roleName = roleName.substring(5); // 移除 "ROLE_" 前缀
                    try {
                        return UserRole.valueOf(roleName);
                    } catch (IllegalArgumentException e) {
                        // 忽略无效的角色名
                    }
                }
            }
        }
        return null;
    }

    /**
     * 检查当前用户是否为管理员
     */
    public static boolean isAdmin() {
        UserRole role = getCurrentUserRole();
        return role != null && role.isAdmin();
    }

    /**
     * 检查当前用户是否为教师
     */
    public static boolean isTeacher() {
        UserRole role = getCurrentUserRole();
        return role != null && role.isTeacher();
    }

    /**
     * 检查当前用户是否为教师或管理员
     */
    public static boolean isTeacherOrAdmin() {
        UserRole role = getCurrentUserRole();
        return role != null && role.hasTeacherOrAdminPermission();
    }

    /**
     * 检查当前用户是否有指定权限
     */
    public static boolean hasRole(UserRole role) {
        UserRole currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equals(role);
    }

    /**
     * 检查当前用户是否有指定权限（多个角色中的一个）
     */
    public static boolean hasAnyRole(UserRole... roles) {
        UserRole currentRole = getCurrentUserRole();
        if (currentRole == null) {
            return false;
        }
        for (UserRole role : roles) {
            if (currentRole.equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            // 如果UserDetails实现类包含用户ID，可以从这里获取
            // 这里假设用户名就是用户ID，实际项目中需要根据具体实现调整
            try {
                return Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                // 如果用户名不是数字，返回默认值
                return 1L;
            }
        }
        // 未登录或获取失败时返回默认值
        return 1L;
    }

    /**
     * 检查是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && !(authentication.getPrincipal() instanceof String);
    }

    /**
     * 检查当前用户是否为学生
     */
    public static boolean isStudent() {
        UserRole role = getCurrentUserRole();
        return role != null && role == UserRole.STUDENT;
    }
}