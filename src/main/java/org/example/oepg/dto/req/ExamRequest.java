package org.example.oepg.dto.req;

import lombok.Data;
import org.example.oepg.entity.Exam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * 考试请求DTO
 */
@Data
public class ExamRequest {

    @NotBlank(message = "考试标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "科目ID不能为空")
    private Long subjectId;

    @NotNull(message = "考试时长不能为空")
    @Positive(message = "考试时长必须为正数")
    private Integer duration; // 考试时长（分钟）

    @NotNull(message = "总分不能为空")
    @Positive(message = "总分必须为正数")
    private Integer totalScore;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private Exam.ExamStatus status = Exam.ExamStatus.DRAFT;
}
