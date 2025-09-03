package org.example.oepg.dto.res;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.oepg.entity.Exam;

import java.time.LocalDateTime;

/**
 * 考试响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {

    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String subjectName; // 科目名称
    private Integer duration;
    private Integer totalScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Exam.ExamStatus status;
    private Long createdById;
    private String createdByName; // 创建者姓名
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 统计信息
    private Integer totalStudents; // 参与学生总数
    private Integer completedCount; // 已完成考试人数
    private Double averageScore; // 平均分
}
