package org.example.oepg.service.impl;

import org.example.oepg.dto.res.RoleInfo;
import org.example.oepg.dto.res.RolePermissions;
import org.example.oepg.enums.UserRole;
import org.example.oepg.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Override
    public List<RoleInfo> getAllRoles() {
        return Arrays.stream(UserRole.values())
                .map(role -> new RoleInfo(role.getCode(), role.getDisplayName(), role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public RoleInfo getRoleByCode(String code) {
        try {
            UserRole role = UserRole.fromCode(code);
            return new RoleInfo(role.getCode(), role.getDisplayName(), role.name());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的角色代码: " + code);
        }
    }

    @Override
    public RolePermissions getRolePermissions(String code) {
        try {
            UserRole role = UserRole.fromCode(code);
            return new RolePermissions(
                    role.getCode(),
                    role.getDisplayName(),
                    role.isStudent(),
                    role.isTeacher(),
                    role.isAdmin(),
                    role.hasTeacherOrAdminPermission(),
                    role.hasAdminPermission()
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的角色代码: " + code);
        }
    }
} 