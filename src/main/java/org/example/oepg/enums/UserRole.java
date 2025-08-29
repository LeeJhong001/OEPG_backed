package org.example.oepg.enums;

/**
 * 用户角色枚举
 * 定义系统中的用户角色类型
 */
public enum UserRole {
    
    /**
     * 学生角色
     * 可以参加考试、查看成绩
     */
    STUDENT("学生", "STUDENT"),
    
    /**
     * 教师角色
     * 可以创建试卷、管理考试、批改试卷
     */
    TEACHER("教师", "TEACHER"),
    
    /**
     * 管理员角色
     * 系统最高权限，可以管理所有功能
     */
    ADMIN("管理员", "ADMIN");
    
    private final String displayName;
    private final String code;
    
    UserRole(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }
    
    /**
     * 获取角色显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取角色代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 根据代码获取角色枚举
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("无效的角色代码: " + code);
    }
    
    /**
     * 根据显示名称获取角色枚举
     */
    public static UserRole fromDisplayName(String displayName) {
        for (UserRole role : values()) {
            if (role.displayName.equals(displayName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("无效的角色名称: " + displayName);
    }
    
    /**
     * 检查是否为学生角色
     */
    public boolean isStudent() {
        return this == STUDENT;
    }
    
    /**
     * 检查是否为教师角色
     */
    public boolean isTeacher() {
        return this == TEACHER;
    }
    
    /**
     * 检查是否为管理员角色
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * 检查是否具有教师或管理员权限
     */
    public boolean hasTeacherOrAdminPermission() {
        return this == TEACHER || this == ADMIN;
    }
    
    /**
     * 检查是否具有管理员权限
     */
    public boolean hasAdminPermission() {
        return this == ADMIN;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 