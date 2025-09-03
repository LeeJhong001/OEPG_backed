package org.example.oepg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 试卷题目关联实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("paper_questions")
public class PaperQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("paper_id")
    private Long paperId; // 使用 ID 而不是对象引用

    @TableField("question_id")
    private Long questionId; // 使用 ID 而不是对象引用

    @TableField("question_order")
    private Integer questionOrder; // 题目顺序

    @TableField("score")
    private Integer score; // 该题分值

    @TableField("section")
    private String section; // 题目所属章节或分类

    @TableField("sort_order")
    private Integer sortOrder; // 题目排序

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 