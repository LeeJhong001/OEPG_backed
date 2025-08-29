package org.example.oepg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 题目实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("questions")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("type")
    private QuestionType type;

    @TableField("difficulty")
    private Integer difficulty;

    @TableField("category_id")
    private Long categoryId; // 使用 ID 而不是对象引用

    @TableField("answer")
    private String answer;

    @TableField("analysis")
    private String analysis;

    @TableField("options")
    private String options; // JSON 格式的选项

    @TableField("score")
    private Integer score;

    @TableField("created_by")
    private Long createdById; // 使用 ID 而不是对象引用

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum QuestionType {
        CHOICE, FILL_BLANK, SHORT_ANSWER, PROOF
    }
} 