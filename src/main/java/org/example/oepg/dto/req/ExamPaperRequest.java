package org.example.oepg.dto.req;

import lombok.Data;
import org.example.oepg.entity.ExamPaper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 试卷请求DTO
 */
@Data
public class ExamPaperRequest {

    @NotBlank(message = "试卷标题不能为空")
    private String title;

    @NotNull(message = "考试ID不能为空")
    private Long examId;

    @NotNull(message = "考试时长不能为空")
    @Positive(message = "考试时长必须为正数")
    private Integer duration; // 考试时长（分钟）

    private ExamPaper.PaperStatus status = ExamPaper.PaperStatus.DRAFT;

    // 智能组卷参数
    private List<QuestionSelectionRule> questionRules;

    /**
     * 题目选择规则
     */
    @Data
    public static class QuestionSelectionRule {
        private Long categoryId; // 分类ID
        private String questionType; // 题目类型
        private Integer difficulty; // 难度等级
        private Integer count; // 题目数量
        private Integer scorePerQuestion; // 每题分数
    }
}
