package org.example.oepg.dto.res;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目使用历史响应 DTO
 */
@Data
@Builder
public class QuestionUsageHistoryResponse {
    
    private Long questionId;
    private String questionTitle;
    private Integer totalUsageCount;
    private Integer correctAnswerCount;
    private Integer incorrectAnswerCount;
    private Double accuracyRate;
    private List<UsageRecord> usageRecords;
    
    @Data
    @Builder
    public static class UsageRecord {
        private Long examId;
        private String examTitle;
        private LocalDateTime usedAt;
        private Integer participantCount;
        private Integer correctCount;
        private Integer incorrectCount;
        private Double accuracyRate;
    }
    
    /**
     * 计算准确率
     */
    public void calculateAccuracyRate() {
        if (totalUsageCount == 0) {
            accuracyRate = 0.0;
        } else {
            accuracyRate = (correctAnswerCount * 100.0) / totalUsageCount;
        }
    }
}
