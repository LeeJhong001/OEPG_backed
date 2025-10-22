package org.example.oepg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 考试记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("exam_records")
public class ExamRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("exam_id")
    private Long examId; // 使用 ID 而不是对象引用

    @TableField("paper_id")
    private Long paperId; // 使用 ID 而不是对象引用

    @TableField("student_id")
    private Long studentId; // 使用 ID 而不是对象引用

    @TableField("score")
    private Integer score; // 得分

    @TableField("total_score")
    private Integer totalScore; // 总分

    @TableField("start_time")
    private LocalDateTime startTime; // 开始时间

    @TableField("submit_time")
    private LocalDateTime submitTime; // 提交时间

    @TableField("time_used")
    private Integer timeUsed; // 用时（分钟）

    @TableField("status")
    private RecordStatus status;

    @TableField("answers")
    private String answers; // 学生答案（JSON字符串）

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum RecordStatus {
        ONGOING, SUBMITTED, GRADED, TIMEOUT
    }
}