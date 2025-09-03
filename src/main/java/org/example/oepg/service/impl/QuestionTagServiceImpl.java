package org.example.oepg.service.impl;

import org.example.oepg.dto.res.QuestionResponse;
import org.example.oepg.entity.QuestionTag;
import org.example.oepg.exception.BusinessException;
import org.example.oepg.repository.QuestionTagRepository;
import org.example.oepg.service.QuestionTagService;
import org.example.oepg.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目标签服务实现类
 */
@Service
public class QuestionTagServiceImpl implements QuestionTagService {

    @Autowired
    private QuestionTagRepository tagRepository;

    @Autowired
    private QuestionService questionService;

    @Override
    @Transactional
    public void updateQuestionTags(Long questionId, List<String> tags) {
        if (questionId == null) {
            throw new BusinessException("INVALID_PARAMETER", "题目ID不能为空");
        }
        
        // 删除原有标签
        tagRepository.deleteByQuestionId(questionId);
        
        // 添加新标签
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    QuestionTag tag = QuestionTag.builder()
                            .questionId(questionId)
                            .tagName(tagName.trim())
                            .build();
                    tagRepository.insert(tag);
                }
            }
        }
    }

    @Override
    public List<QuestionResponse> getQuestionsByTag(String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new BusinessException("INVALID_PARAMETER", "标签名不能为空");
        }
        
        List<Long> questionIds = tagRepository.findQuestionIdsByTagName(tagName.trim());
        return questionIds.stream()
                .map(questionService::getQuestionById)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getQuestionTags(Long questionId) {
        if (questionId == null) {
            throw new BusinessException("INVALID_PARAMETER", "题目ID不能为空");
        }
        
        return tagRepository.findTagNamesByQuestionId(questionId);
    }

    @Override
    public List<String> getAllTags() {
        return tagRepository.findAllTagNames();
    }
}
