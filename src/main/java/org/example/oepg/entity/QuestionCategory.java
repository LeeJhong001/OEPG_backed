package org.example.oepg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 题目分类实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("question_categories")
public class QuestionCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("parent_id")
    private Long parentId; // 父分类ID，用于构建分类树结构

    @TableField("description")
    private String description;

    @TableField("sort_order")
    private Integer sortOrder = 0; // 分类排序顺序，数值越小排序越靠前

    @TableField("enabled")
    private Boolean enabled = true; // 是否启用

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 