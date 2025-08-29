package org.example.oepg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 考试实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("exams")
public class Exam {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("subject_id")
    private Long subjectId; // 使用 ID 而不是对象引用

    @TableField("duration")
    private Integer duration; // 考试时长（分钟）

    @TableField("total_score")
    private Integer totalScore; // 总分

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("status")
    private ExamStatus status;

    @TableField("created_by")
    private Long createdById; // 使用 ID 而不是对象引用

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum ExamStatus {
        DRAFT, PUBLISHED, ONGOING, FINISHED, ARCHIVED
    }
} 