package org.example.oepg.dto.res;

import lombok.Data;

/**
 * 题目统计信息响应 DTO
 */
@Data
public class QuestionStatisticsResponse {
    
    private Long totalCount;
    private Long choiceCount;
    private Long fillBlankCount;
    private Long shortAnswerCount;
    private Long proofCount;
    private Long easyCount;
    private Long mediumCount;
    private Long hardCount;
    
    // 百分比信息
    private Double choicePercentage;
    private Double fillBlankPercentage;
    private Double shortAnswerPercentage;
    private Double proofPercentage;
    private Double easyPercentage;
    private Double mediumPercentage;
    private Double hardPercentage;
    
    /**
     * 计算百分比
     */
    public void calculatePercentages() {
        if (totalCount == 0) {
            return;
        }
        
        choicePercentage = (choiceCount * 100.0) / totalCount;
        fillBlankPercentage = (fillBlankCount * 100.0) / totalCount;
        shortAnswerPercentage = (shortAnswerCount * 100.0) / totalCount;
        proofPercentage = (proofCount * 100.0) / totalCount;
        easyPercentage = (easyCount * 100.0) / totalCount;
        mediumPercentage = (mediumCount * 100.0) / totalCount;
        hardPercentage = (hardCount * 100.0) / totalCount;
    }
}
