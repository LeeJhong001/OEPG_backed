package org.example.oepg.service;

import org.example.oepg.dto.res.QuestionResponse;

import java.util.List;

/**
 * 题目收藏服务接口
 */
public interface QuestionFavoriteService {

    /**
     * 切换题目收藏状态
     */
    boolean toggleFavorite(Long userId, Long questionId);

    /**
     * 获取用户收藏的题目列表
     */
    List<QuestionResponse> getFavoriteQuestions(Long userId);

    /**
     * 检查用户是否收藏了某个题目
     */
    boolean isFavorite(Long userId, Long questionId);
}
