package org.example.oepg.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 分类请求 DTO
 */
@Data
public class CategoryRequest {
    
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;
    
    private Long parentId; // 父分类ID，可以为空（顶级分类）
    
    @Size(max = 500, message = "分类描述长度不能超过500个字符")
    private String description;
    
    private Integer sortOrder = 0;
    
    private Boolean enabled = true;
} 