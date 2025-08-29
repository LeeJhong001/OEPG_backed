package org.example.oepg.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色信息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleInfo {
    private String code;
    private String displayName;
    private String enumName;
} 