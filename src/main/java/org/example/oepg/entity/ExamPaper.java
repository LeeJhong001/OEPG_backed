package org.example.oepg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 试卷实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("exam_papers")
public class ExamPaper {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("exam_id")
    private Long examId; // 使用 ID 而不是对象引用

    @TableField("total_questions")
    private Integer totalQuestions; // 总题数

    @TableField("total_score")
    private Integer totalScore; // 总分

    @TableField("duration")
    private Integer duration; // 考试时长（分钟）

    @TableField("status")
    private PaperStatus status;

    @TableField("created_by")
    private Long createdById; // 使用 ID 而不是对象引用

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum PaperStatus {
        DRAFT, PUBLISHED, ARCHIVED
    }
} 