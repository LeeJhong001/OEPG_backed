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
    
    /**
     * 复制题目
     */
    QuestionResponse copyQuestion(Long id);
    
    /**
     * 随机获取题目（用于组卷）
     */
    List<QuestionResponse> getRandomQuestions(int count, Long categoryId, Question.QuestionType type, Integer difficulty);
    
    /**
     * 批量更新题目状态
     */
    void batchUpdateQuestionStatus(List<Long> ids, boolean enabled);
    
    /**
     * 批量更新题目分类
     */
    void batchUpdateQuestionCategory(List<Long> ids, Long categoryId);
    
    /**
     * 获取题目统计信息
     */
    Object getQuestionStatistics();
    
    /**
     * 根据难度分布获取题目
     */
    List<QuestionResponse> getQuestionsByDifficultyDistribution(Long categoryId, int easy, int medium, int hard);
    
    /**
     * 获取搜索建议
     */
    List<String> getSearchSuggestions(String keyword);
    
    /**
     * 验证题目答案
     */
    Object validateAnswer(Long id, String userAnswer);
    
    /**
     * 获取题目使用记录
     */
    Object getQuestionUsageHistory(Long id);
} 