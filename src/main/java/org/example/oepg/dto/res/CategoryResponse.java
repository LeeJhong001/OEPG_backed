package org.example.oepg.dto.res;

import lombok.Data;
import org.example.oepg.entity.QuestionCategory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类响应 DTO
 */
@Data
public class CategoryResponse {
    
    private Long id;
    private String name;
    private Long parentId;
    private String description;
    private Integer sortOrder;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 树形结构相关字段
    private List<CategoryResponse> children;
    private Boolean hasChildren;
    private Integer level; // 分类层级，顶级为0
    
    /**
     * 从 QuestionCategory 实体创建 CategoryResponse
     */
    public static CategoryResponse fromEntity(QuestionCategory category) {
        if (category == null) {
            return null;
        }
        
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setParentId(category.getParentId());
        response.setDescription(category.getDescription());
        response.setSortOrder(category.getSortOrder());
        response.setEnabled(category.getEnabled());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        
        return response;
    }
} 