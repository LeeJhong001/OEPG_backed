package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.oepg.dto.req.QuestionRequest;
import org.example.oepg.dto.res.QuestionResponse;
import org.example.oepg.dto.res.QuestionStatisticsResponse;
import org.example.oepg.dto.res.AnswerValidationResponse;
import org.example.oepg.dto.res.QuestionUsageHistoryResponse;
import org.example.oepg.entity.Question;
import org.example.oepg.entity.User;
import org.example.oepg.repository.QuestionRepository;
import org.example.oepg.repository.UserRepository;
import org.example.oepg.service.QuestionService;
import org.example.oepg.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 题目服务实现类
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public QuestionResponse createQuestion(QuestionRequest request) {
        // 权限检查：只有教师和管理员可以创建题目
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以创建题目");
        }

        // 从认证信息中获取用户ID
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("用户未认证");
        }
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .difficulty(request.getDifficulty())
                .categoryId(request.getCategoryId())
                .answer(request.getAnswer())
                .analysis(request.getAnalysis())
                .options(request.getOptions())
                .score(request.getScore())
                .createdById(user.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        questionRepository.insert(question);
        return QuestionResponse.fromEntity(question);
    }

    @Override
    public QuestionResponse updateQuestion(Long id, QuestionRequest request) {
        Question question = questionRepository.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 权限检查：管理员可以编辑所有题目，教师只能编辑自己创建的题目
        if (!SecurityUtil.isAdmin()) {
            if (!SecurityUtil.isTeacher()) {
                throw new RuntimeException("权限不足：只有教师和管理员可以编辑题目");
            }
            
            // 检查是否是自己的题目
            String currentUsername = SecurityUtil.getCurrentUsername();
            if (currentUsername != null) {
                User currentUser = userRepository.findByUsername(currentUsername);
                if (currentUser == null || !currentUser.getId().equals(question.getCreatedById())) {
                    throw new RuntimeException("权限不足：只能编辑自己创建的题目");
                }
            }
        }

        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setType(request.getType());
        question.setDifficulty(request.getDifficulty());
        question.setCategoryId(request.getCategoryId());
        question.setAnswer(request.getAnswer());
        question.setAnalysis(request.getAnalysis());
        question.setOptions(request.getOptions());
        question.setScore(request.getScore());
        question.setUpdatedAt(LocalDateTime.now());

        questionRepository.updateById(question);
        return QuestionResponse.fromEntity(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        Question question = questionRepository.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 权限检查：管理员可以删除所有题目，教师只能删除自己创建的题目
        if (!SecurityUtil.isAdmin()) {
            if (!SecurityUtil.isTeacher()) {
                throw new RuntimeException("权限不足：只有教师和管理员可以删除题目");
            }
            
            // 检查是否是自己的题目
            String currentUsername = SecurityUtil.getCurrentUsername();
            if (currentUsername != null) {
                User currentUser = userRepository.findByUsername(currentUsername);
                if (currentUser == null || !currentUser.getId().equals(question.getCreatedById())) {
                    throw new RuntimeException("权限不足：只能删除自己创建的题目");
                }
            }
        }

        questionRepository.deleteById(id);
    }

    @Override
    public QuestionResponse getQuestionById(Long id) {
        // 权限检查：教师和管理员可以查看题目
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以查看题目");
        }

        Question question = questionRepository.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        return QuestionResponse.fromEntity(question);
    }

    @Override
    public IPage<QuestionResponse> getQuestions(int page, int size, Long categoryId, 
                                              Question.QuestionType type, Integer difficulty, String keyword) {
        // 权限检查：教师和管理员可以查看题目
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以查看题目");
        }

        Page<Question> pageParam = new Page<>(page, size);
        IPage<Question> questionPage = questionRepository.findQuestionsWithCategory(
                pageParam, categoryId, type, difficulty, keyword);

        // 转换为 QuestionResponse
        IPage<QuestionResponse> responsePage = new Page<>(page, size);
        responsePage.setTotal(questionPage.getTotal());
        responsePage.setCurrent(questionPage.getCurrent());
        responsePage.setSize(questionPage.getSize());

        List<QuestionResponse> responses = questionPage.getRecords().stream()
                .map(QuestionResponse::fromEntity)
                .collect(Collectors.toList());
        responsePage.setRecords(responses);

        return responsePage;
    }

    @Override
    public List<QuestionResponse> getQuestionsByCategory(Long categoryId) {
        // 权限检查：教师和管理员可以查看题目
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以查看题目");
        }

        List<Question> questions = questionRepository.selectList(null);
        return questions.stream()
                .filter(q -> categoryId.equals(q.getCategoryId()))
                .map(QuestionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getQuestionsByCreator(Long createdById) {
        // 权限检查：教师和管理员可以查看题目
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以查看题目");
        }

        List<Question> questions = questionRepository.findByCreatedById(createdById);
        return questions.stream()
                .map(QuestionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void batchDeleteQuestions(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 权限检查：管理员可以批量删除所有题目，教师只能批量删除自己的题目
        if (!SecurityUtil.isAdmin()) {
            if (!SecurityUtil.isTeacher()) {
                throw new RuntimeException("权限不足：只有教师和管理员可以删除题目");
            }
            
            // 检查是否都是自己的题目
            String currentUsername = SecurityUtil.getCurrentUsername();
            if (currentUsername != null) {
                User currentUser = userRepository.findByUsername(currentUsername);
                if (currentUser != null) {
                    for (Long id : ids) {
                        Question question = questionRepository.selectById(id);
                        if (question != null && !currentUser.getId().equals(question.getCreatedById())) {
                            throw new RuntimeException("权限不足：只能删除自己创建的题目");
                        }
                    }
                }
            }
        }

        for (Long id : ids) {
            deleteQuestion(id);
        }
    }

    @Override
    public QuestionResponse copyQuestion(Long id) {
        // 权限检查：只有教师和管理员可以复制题目
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以复制题目");
        }

        Question originalQuestion = questionRepository.selectById(id);
        if (originalQuestion == null) {
            throw new RuntimeException("题目不存在");
        }

        // 获取当前用户信息
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("用户未认证");
        }
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 创建复制的题目
        Question copiedQuestion = Question.builder()
                .title(originalQuestion.getTitle() + " (复制)")
                .content(originalQuestion.getContent())
                .type(originalQuestion.getType())
                .difficulty(originalQuestion.getDifficulty())
                .categoryId(originalQuestion.getCategoryId())
                .answer(originalQuestion.getAnswer())
                .analysis(originalQuestion.getAnalysis())
                .options(originalQuestion.getOptions())
                .score(originalQuestion.getScore())
                .createdById(user.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        questionRepository.insert(copiedQuestion);
        return QuestionResponse.fromEntity(copiedQuestion);
    }

    @Override
    public List<QuestionResponse> getRandomQuestions(int count, Long categoryId, Question.QuestionType type, Integer difficulty) {
        // 权限检查：教师和管理员可以获取随机题目
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以获取随机题目");
        }

        List<Question> questions = questionRepository.findRandomQuestions(count, categoryId, type, difficulty);
        return questions.stream()
                .map(QuestionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void batchUpdateQuestionStatus(List<Long> ids, boolean enabled) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 权限检查：管理员可以批量更新所有题目状态，教师只能更新自己的题目
        if (!SecurityUtil.isAdmin()) {
            if (!SecurityUtil.isTeacher()) {
                throw new RuntimeException("权限不足：只有教师和管理员可以更新题目状态");
            }
            
            // 检查是否都是自己的题目
            String currentUsername = SecurityUtil.getCurrentUsername();
            if (currentUsername != null) {
                User currentUser = userRepository.findByUsername(currentUsername);
                if (currentUser != null) {
                    for (Long id : ids) {
                        Question question = questionRepository.selectById(id);
                        if (question != null && !currentUser.getId().equals(question.getCreatedById())) {
                            throw new RuntimeException("权限不足：只能更新自己创建的题目");
                        }
                    }
                }
            }
        }

        questionRepository.batchUpdateStatus(ids, enabled);
    }

    @Override
    public void batchUpdateQuestionCategory(List<Long> ids, Long categoryId) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 权限检查：管理员可以批量更新所有题目分类，教师只能更新自己的题目
        if (!SecurityUtil.isAdmin()) {
            if (!SecurityUtil.isTeacher()) {
                throw new RuntimeException("权限不足：只有教师和管理员可以更新题目分类");
            }
            
            // 检查是否都是自己的题目
            String currentUsername = SecurityUtil.getCurrentUsername();
            if (currentUsername != null) {
                User currentUser = userRepository.findByUsername(currentUsername);
                if (currentUser != null) {
                    for (Long id : ids) {
                        Question question = questionRepository.selectById(id);
                        if (question != null && !currentUser.getId().equals(question.getCreatedById())) {
                            throw new RuntimeException("权限不足：只能更新自己创建的题目");
                        }
                    }
                }
            }
        }

        questionRepository.batchUpdateCategory(ids, categoryId);
    }

    @Override
    public Object getQuestionStatistics() {
        // 权限检查：教师和管理员可以查看统计信息
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以查看统计信息");
        }

        // 这里需要根据实际的查询结果结构来处理
        // 暂时返回一个模拟的统计信息
        Object rawStatistics = questionRepository.getQuestionStatistics();
        
        // 如果 getQuestionStatistics 返回的是 Map，需要转换为我们的 DTO
        if (rawStatistics instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> statsMap = (Map<String, Object>) rawStatistics;
            
            QuestionStatisticsResponse response = new QuestionStatisticsResponse();
            response.setTotalCount(((Number) statsMap.getOrDefault("totalCount", 0)).longValue());
            response.setChoiceCount(((Number) statsMap.getOrDefault("choiceCount", 0)).longValue());
            response.setFillBlankCount(((Number) statsMap.getOrDefault("fillBlankCount", 0)).longValue());
            response.setShortAnswerCount(((Number) statsMap.getOrDefault("shortAnswerCount", 0)).longValue());
            response.setProofCount(((Number) statsMap.getOrDefault("proofCount", 0)).longValue());
            response.setEasyCount(((Number) statsMap.getOrDefault("easyCount", 0)).longValue());
            response.setMediumCount(((Number) statsMap.getOrDefault("mediumCount", 0)).longValue());
            response.setHardCount(((Number) statsMap.getOrDefault("hardCount", 0)).longValue());
            response.calculatePercentages();
            
            return response;
        }
        
        return rawStatistics;
    }

    @Override
    public List<QuestionResponse> getQuestionsByDifficultyDistribution(Long categoryId, int easy, int medium, int hard) {
        // 权限检查：教师和管理员可以按难度分布获取题目
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以按难度分布获取题目");
        }

        List<Question> questions = questionRepository.findQuestionsByDifficultyDistribution(categoryId, easy, medium, hard);
        return questions.stream()
                .map(QuestionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSearchSuggestions(String keyword) {
        // 权限检查：教师和管理员可以获取搜索建议
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以获取搜索建议");
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return questionRepository.getSearchSuggestions(keyword.trim());
    }

    @Override
    public Object validateAnswer(Long id, String userAnswer) {
        // 权限检查：所有认证用户都可以验证答案
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("用户未认证");
        }

        Question question = questionRepository.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return AnswerValidationResponse.incorrect(userAnswer, question.getAnswer(), question.getAnalysis());
        }

        // 根据题目类型进行不同的答案验证
        boolean isCorrect = false;
        String trimmedUserAnswer = userAnswer.trim();
        String correctAnswer = question.getAnswer();

        switch (question.getType()) {
            case CHOICE:
                // 选择题：直接比较答案
                isCorrect = trimmedUserAnswer.equalsIgnoreCase(correctAnswer);
                break;
            case FILL_BLANK:
                // 填空题：去除空格后比较
                isCorrect = trimmedUserAnswer.replaceAll("\\s+", "").equalsIgnoreCase(correctAnswer.replaceAll("\\s+", ""));
                break;
            case SHORT_ANSWER:
            case PROOF:
                // 简答题和证明题：包含关键词即可（这里简化处理）
                // 实际项目中可以使用更复杂的自然语言处理算法
                String[] keywords = correctAnswer.split("[,，;；]");
                isCorrect = true;
                for (String keyword : keywords) {
                    if (!trimmedUserAnswer.toLowerCase().contains(keyword.trim().toLowerCase())) {
                        isCorrect = false;
                        break;
                    }
                }
                break;
        }

        if (isCorrect) {
            return AnswerValidationResponse.correct(userAnswer, correctAnswer, question.getScore(), question.getAnalysis());
        } else {
            return AnswerValidationResponse.incorrect(userAnswer, correctAnswer, question.getAnalysis());
        }
    }

    @Override
    public Object getQuestionUsageHistory(Long id) {
        // 权限检查：教师和管理员可以查看题目使用历史
        if (!SecurityUtil.isTeacherOrAdmin()) {
            throw new RuntimeException("权限不足：只有教师和管理员可以查看题目使用历史");
        }

        Question question = questionRepository.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 模拟使用历史数据（实际项目中需要从考试记录表中查询）
        // 这里需要根据实际的数据库设计来实现
        QuestionUsageHistoryResponse response = QuestionUsageHistoryResponse.builder()
                .questionId(id)
                .questionTitle(question.getTitle())
                .totalUsageCount(0)
                .correctAnswerCount(0)
                .incorrectAnswerCount(0)
                .accuracyRate(0.0)
                .usageRecords(new ArrayList<>())
                .build();

        response.calculateAccuracyRate();
        return response;
    }
} 