package org.example.oepg.dto.req;

import lombok.Data;
import org.example.oepg.entity.User;
import org.example.oepg.enums.UserRole;

/**
 * 用户查询请求 DTO
 * 管理员查询用户列表时使用
 */
@Data
public class UserQueryRequest {
    
    /**
     * 当前页码（从1开始）
     */
    private Integer current = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
    
    /**
     * 用户名（模糊查询）
     */
    private String username;
    
    /**
     * 真实姓名（模糊查询）
     */
    private String realName;
    
    /**
     * 邮箱（模糊查询）
     */
    private String email;
    
    /**
     * 用户角色
     */
    private UserRole role;
    
    /**
     * 用户状态
     */
    private User.UserStatus status;
    
    /**
     * 排序字段
     */
    private String sortField = "createdAt";
    
    /**
     * 排序方向：asc/desc
     */
    private String sortOrder = "desc";
}
