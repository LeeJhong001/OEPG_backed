package org.example.oepg.dto.res;

import lombok.Data;
import org.example.oepg.entity.Question;

import java.time.LocalDateTime;

/**
 * 题目响应 DTO
 */
@Data
public class QuestionResponse {
    
    private Long id;
    private String title;
    private String content;
    private Question.QuestionType type;
    private String typeDisplayName;
    private Integer difficulty;
    private Long categoryId;
    private String categoryName;
    private String answer;
    private String analysis;
    private String options;
    private Integer score;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 从 Question 实体创建 QuestionResponse
     */
    public static QuestionResponse fromEntity(Question question) {
        if (question == null) {
            return null;
        }
        
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setTitle(question.getTitle());
        response.setContent(question.getContent());
        response.setType(question.getType());
        response.setTypeDisplayName(getTypeDisplayName(question.getType()));
        response.setDifficulty(question.getDifficulty());
        response.setCategoryId(question.getCategoryId());
        response.setAnswer(question.getAnswer());
        response.setAnalysis(question.getAnalysis());
        response.setOptions(question.getOptions());
        response.setScore(question.getScore());
        response.setCreatedById(question.getCreatedById());
        response.setCreatedAt(question.getCreatedAt());
        response.setUpdatedAt(question.getUpdatedAt());
        
        return response;
    }
    
    /**
     * 获取题目类型显示名称
     */
    private static String getTypeDisplayName(Question.QuestionType type) {
        if (type == null) return "";
        
        switch (type) {
            case CHOICE: return "选择题";
            case FILL_BLANK: return "填空题";
            case SHORT_ANSWER: return "简答题";
            case PROOF: return "证明题";
            default: return type.name();
        }
    }
} 