package org.example.oepg.dto.res;

import lombok.Data;
import lombok.Builder;

/**
 * 答案验证响应 DTO
 */
@Data
@Builder
public class AnswerValidationResponse {
    
    private Boolean isCorrect;
    private String userAnswer;
    private String correctAnswer;
    private String explanation;
    private Integer score;
    private String feedback;
    
    /**
     * 创建正确答案响应
     */
    public static AnswerValidationResponse correct(String userAnswer, String correctAnswer, Integer score, String explanation) {
        return AnswerValidationResponse.builder()
                .isCorrect(true)
                .userAnswer(userAnswer)
                .correctAnswer(correctAnswer)
                .score(score)
                .explanation(explanation)
                .feedback("答案正确！")
                .build();
    }
    
    /**
     * 创建错误答案响应
     */
    public static AnswerValidationResponse incorrect(String userAnswer, String correctAnswer, String explanation) {
        return AnswerValidationResponse.builder()
                .isCorrect(false)
                .userAnswer(userAnswer)
                .correctAnswer(correctAnswer)
                .score(0)
                .explanation(explanation)
                .feedback("答案错误，请重新思考。")
                .build();
    }
}
