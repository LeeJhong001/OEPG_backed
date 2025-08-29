package org.example.oepg.constants;

/**
 * 角色相关常量
 * 定义系统中角色相关的常量值
 */
public class RoleConstants {

    /**
     * 角色代码常量
     */
    public static class Codes {
        public static final String STUDENT = "STUDENT";
        public static final String TEACHER = "TEACHER";
        public static final String ADMIN = "ADMIN";
    }

    /**
     * 角色显示名称常量
     */
    public static class DisplayNames {
        public static final String STUDENT = "学生";
        public static final String TEACHER = "教师";
        public static final String ADMIN = "管理员";
    }

    /**
     * Spring Security 角色前缀
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 完整角色名称常量
     */
    public static class FullNames {
        public static final String ROLE_STUDENT = ROLE_PREFIX + Codes.STUDENT;
        public static final String ROLE_TEACHER = ROLE_PREFIX + Codes.TEACHER;
        public static final String ROLE_ADMIN = ROLE_PREFIX + Codes.ADMIN;
    }

    /**
     * 角色描述常量
     */
    public static class Descriptions {
        public static final String STUDENT = "可以参加考试、查看成绩";
        public static final String TEACHER = "可以创建试卷、管理考试、批改试卷";
        public static final String ADMIN = "系统最高权限，可以管理所有功能";
    }

    /**
     * 角色权限级别
     */
    public static class PermissionLevels {
        public static final int STUDENT = 1;
        public static final int TEACHER = 2;
        public static final int ADMIN = 3;
    }

    /**
     * 私有构造函数，防止实例化
     */
    private RoleConstants() {
        throw new UnsupportedOperationException("这是一个常量类，不能实例化");
    }
} 