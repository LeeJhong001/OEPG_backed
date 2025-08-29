package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.oepg.dto.req.QuestionRequest;
import org.example.oepg.dto.res.QuestionResponse;
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
} 