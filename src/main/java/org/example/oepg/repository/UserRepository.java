package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.oepg.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {
    
    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
    
    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);
    
    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    boolean existsByEmail(@Param("email") String email);
} 