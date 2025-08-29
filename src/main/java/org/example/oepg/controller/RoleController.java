package org.example.oepg.controller;

import org.example.oepg.dto.res.RoleInfo;
import org.example.oepg.dto.res.RolePermissions;
import org.example.oepg.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 * 提供角色查询和管理功能
 */
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取所有可用角色
     */
    @GetMapping
    public ResponseEntity<List<RoleInfo>> getAllRoles() {
        try {
            List<RoleInfo> roles = roleService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据角色代码获取角色信息
     */
    @GetMapping("/{code}")
    public ResponseEntity<RoleInfo> getRoleByCode(@PathVariable String code) {
        try {
            RoleInfo roleInfo = roleService.getRoleByCode(code);
            return ResponseEntity.ok(roleInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取角色权限信息
     */
    @GetMapping("/{code}/permissions")
    public ResponseEntity<RolePermissions> getRolePermissions(@PathVariable String code) {
        try {
            RolePermissions permissions = roleService.getRolePermissions(code);
            return ResponseEntity.ok(permissions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 