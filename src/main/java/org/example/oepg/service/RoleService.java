package org.example.oepg.service;

import org.example.oepg.dto.res.RoleInfo;
import org.example.oepg.dto.res.RolePermissions;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {
    
    /**
     * 获取所有可用角色
     */
    List<RoleInfo> getAllRoles();
    
    /**
     * 根据角色代码获取角色信息
     */
    RoleInfo getRoleByCode(String code);
    
    /**
     * 获取角色权限信息
     */
    RolePermissions getRolePermissions(String code);
} 