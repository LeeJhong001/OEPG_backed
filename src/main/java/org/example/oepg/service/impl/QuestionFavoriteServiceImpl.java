package org.example.oepg.service.impl;

import org.example.oepg.dto.res.QuestionResponse;
import org.example.oepg.entity.QuestionFavorite;
import org.example.oepg.exception.BusinessException;
import org.example.oepg.repository.QuestionFavoriteRepository;
import org.example.oepg.service.QuestionFavoriteService;
import org.example.oepg.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目收藏服务实现类
 */
@Service
public class QuestionFavoriteServiceImpl implements QuestionFavoriteService {

    @Autowired
    private QuestionFavoriteRepository favoriteRepository;

    @Autowired
    private QuestionService questionService;

    @Override
    public boolean toggleFavorite(Long userId, Long questionId) {
        if (userId == null || questionId == null) {
            throw new BusinessException("INVALID_PARAMETER", "用户ID和题目ID不能为空");
        }
        
        boolean isFavorite = favoriteRepository.existsByUserIdAndQuestionId(userId, questionId);
        
        if (isFavorite) {
            // 取消收藏
            favoriteRepository.deleteByUserIdAndQuestionId(userId, questionId);
            return false;
        } else {
            // 添加收藏
            QuestionFavorite favorite = QuestionFavorite.builder()
                    .userId(userId)
                    .questionId(questionId)
                    .build();
            favoriteRepository.insert(favorite);
            return true;
        }
    }

    @Override
    public List<QuestionResponse> getFavoriteQuestions(Long userId) {
        if (userId == null) {
            throw new BusinessException("INVALID_PARAMETER", "用户ID不能为空");
        }
        
        List<Long> questionIds = favoriteRepository.findQuestionIdsByUserId(userId);
        return questionIds.stream()
                .map(questionService::getQuestionById)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorite(Long userId, Long questionId) {
        if (userId == null || questionId == null) {
            throw new BusinessException("INVALID_PARAMETER", "用户ID和题目ID不能为空");
        }
        
        return favoriteRepository.existsByUserIdAndQuestionId(userId, questionId);
    }
}
