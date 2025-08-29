package org.example.oepg.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.oepg.dto.req.QuestionRequest;
import org.example.oepg.dto.res.QuestionResponse;
import org.example.oepg.entity.Question;

import java.util.List;

/**
 * 题目服务接口
 */
public interface QuestionService {
    
    /**
     * 创建题目
     */
    QuestionResponse createQuestion(QuestionRequest request);
    
    /**
     * 更新题目
     */
    QuestionResponse updateQuestion(Long id, QuestionRequest request);
    
    /**
     * 删除题目
     */
    void deleteQuestion(Long id);
    
    /**
     * 根据ID获取题目
     */
    QuestionResponse getQuestionById(Long id);
    
    /**
     * 分页查询题目
     */
    IPage<QuestionResponse> getQuestions(int page, int size, Long categoryId, 
                                       Question.QuestionType type, Integer difficulty, String keyword);
    
    /**
     * 根据分类ID获取题目
     */
    List<QuestionResponse> getQuestionsByCategory(Long categoryId);
    
    /**
     * 根据创建者ID获取题目
     */
    List<QuestionResponse> getQuestionsByCreator(Long createdById);
    
    /**
     * 批量删除题目
     */
    void batchDeleteQuestions(List<Long> ids);
} 