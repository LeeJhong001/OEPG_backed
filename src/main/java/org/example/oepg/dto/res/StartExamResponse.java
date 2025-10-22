package org.example.oepg.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartExamResponse {
    private Long recordId;
    private Long examId;
    private Long paperId;
    private String examTitle;
    private Integer durationMinutes;
    private Integer totalScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<ExamQuestion> questions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExamQuestion {
        private Long questionId;
        private String title;
        private String content;
        private String type;
        private Integer difficulty;
        private Integer score;
        private Integer sortOrder;
        private String options; // 仅用于选择题
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvailableExam {
        private Long examId;
        private String title;
        private Integer durationMinutes;
        private Integer totalScore;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
