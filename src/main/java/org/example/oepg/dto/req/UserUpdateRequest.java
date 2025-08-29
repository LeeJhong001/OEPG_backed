package org.example.oepg.dto.req;

import lombok.Data;
import org.example.oepg.entity.User;
import org.example.oepg.enums.UserRole;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * 更新用户请求 DTO
 * 管理员更新用户信息时使用
 */
@Data
public class UserUpdateRequest {
    
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;
    
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    @Email(message = "邮箱格式不正确")
    private String email;

    private UserRole role;
    
    private User.UserStatus status;
}
