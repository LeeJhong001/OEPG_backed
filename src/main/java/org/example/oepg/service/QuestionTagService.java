package org.example.oepg.service;

import org.example.oepg.dto.res.QuestionResponse;

import java.util.List;

/**
 * 题目标签服务接口
 */
public interface QuestionTagService {

    /**
     * 更新题目标签
     */
    void updateQuestionTags(Long questionId, List<String> tags);

    /**
     * 根据标签获取题目列表
     */
    List<QuestionResponse> getQuestionsByTag(String tagName);

    /**
     * 获取题目的标签列表
     */
    List<String> getQuestionTags(Long questionId);

    /**
     * 获取所有标签
     */
    List<String> getAllTags();
}
