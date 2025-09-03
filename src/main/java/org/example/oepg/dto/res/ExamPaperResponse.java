package org.example.oepg.dto.res;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.oepg.entity.ExamPaper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamPaperResponse {

    private Long id;
    private String title;
    private Long examId;
    private String examTitle; // 考试标题
    private Integer totalQuestions;
    private Integer totalScore;
    private Integer duration;
    private ExamPaper.PaperStatus status;
    private Long createdById;
    private String createdByName; // 创建者姓名
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 试卷题目列表
    private List<PaperQuestionInfo> questions;
    
    /**
     * 试卷题目信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaperQuestionInfo {
        private Long questionId;
        private String questionTitle;
        private String questionContent;
        private String questionType;
        private Integer difficulty;
        private Integer score;
        private Integer sortOrder; // 题目在试卷中的顺序
        private String options; // 选择题选项
    }
}
