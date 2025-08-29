package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    
    /**
     * 检查用户名是否存在（排除指定用户ID）
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username} AND id != #{userId}")
    boolean existsByUsernameAndNotId(@Param("username") String username, @Param("userId") Long userId);
    
    /**
     * 检查邮箱是否存在（排除指定用户ID）
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email} AND id != #{userId}")
    boolean existsByEmailAndNotId(@Param("email") String email, @Param("userId") Long userId);
    
    /**
     * 分页查询用户列表（带条件查询）
     * 使用动态SQL来构建查询条件
     */
    IPage<User> selectPageWithConditions(Page<User> page, 
                                        @Param("username") String username,
                                        @Param("realName") String realName,
                                        @Param("email") String email,
                                        @Param("role") String role,
                                        @Param("status") String status,
                                        @Param("sortField") String sortField,
                                        @Param("sortOrder") String sortOrder);
} 