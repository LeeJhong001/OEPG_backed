package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.oepg.dto.req.ExamPaperRequest;
import org.example.oepg.dto.res.ExamPaperResponse;
import org.example.oepg.entity.ExamPaper;
import org.example.oepg.entity.PaperQuestion;
import org.example.oepg.entity.Question;
import org.example.oepg.repository.ExamPaperRepository;
import org.example.oepg.repository.PaperQuestionRepository;
import org.example.oepg.repository.QuestionRepository;
import org.example.oepg.service.ExamPaperService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 试卷服务实现类
 */
@Service
@Transactional
public class ExamPaperServiceImpl implements ExamPaperService {

    @Autowired
    private ExamPaperRepository examPaperRepository;

    @Autowired
    private PaperQuestionRepository paperQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public ExamPaperResponse createExamPaper(ExamPaperRequest request) {
        ExamPaper examPaper = ExamPaper.builder()
                .title(request.getTitle())
                .examId(request.getExamId())
                .duration(request.getDuration())
                .status(request.getStatus())
                .totalQuestions(0)
                .totalScore(0)
                .createdById(getCurrentUserId())
                .build();

        examPaperRepository.insert(examPaper);
        return convertToResponse(examPaper);
    }

    @Override
    public ExamPaperResponse generatePaperByRules(ExamPaperRequest request) {
        // 创建试卷
        ExamPaper examPaper = ExamPaper.builder()
                .title(request.getTitle())
                .examId(request.getExamId())
                .duration(request.getDuration())
                .status(ExamPaper.PaperStatus.DRAFT)
                .createdById(getCurrentUserId())
                .build();

        examPaperRepository.insert(examPaper);

        // 智能组卷逻辑
        List<PaperQuestion> paperQuestions = new ArrayList<>();
        int totalScore = 0;
        int sortOrder = 1;

        for (ExamPaperRequest.QuestionSelectionRule rule : request.getQuestionRules()) {
            List<Question> selectedQuestions = selectQuestionsByRule(rule);
            
            for (Question question : selectedQuestions) {
                PaperQuestion paperQuestion = PaperQuestion.builder()
                        .paperId(examPaper.getId())
                        .questionId(question.getId())
                        .score(rule.getScorePerQuestion())
                        .sortOrder(sortOrder++)
                        .build();
                
                paperQuestionRepository.insert(paperQuestion);
                paperQuestions.add(paperQuestion);
                totalScore += rule.getScorePerQuestion();
            }
        }

        // 更新试卷统计信息
        examPaper.setTotalQuestions(paperQuestions.size());
        examPaper.setTotalScore(totalScore);
        examPaperRepository.updateById(examPaper);

        return convertToResponse(examPaper);
    }

    @Override
    public ExamPaperResponse updateExamPaper(Long id, ExamPaperRequest request) {
        ExamPaper examPaper = examPaperRepository.selectById(id);
        if (examPaper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 验证权限
        if (!examPaper.getCreatedById().equals(getCurrentUserId())) {
            throw new RuntimeException("无权限修改此试卷");
        }

        examPaper.setTitle(request.getTitle());
        examPaper.setExamId(request.getExamId());
        examPaper.setDuration(request.getDuration());
        examPaper.setStatus(request.getStatus());

        examPaperRepository.updateById(examPaper);
        return convertToResponse(examPaper);
    }

    @Override
    public void deleteExamPaper(Long id) {
        ExamPaper examPaper = examPaperRepository.selectById(id);
        if (examPaper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 验证权限
        if (!examPaper.getCreatedById().equals(getCurrentUserId())) {
            throw new RuntimeException("无权限删除此试卷");
        }

        // 检查试卷状态
        if (examPaper.getStatus() == ExamPaper.PaperStatus.PUBLISHED) {
            throw new RuntimeException("已发布的试卷不能删除");
        }

        // 删除试卷关联的题目
        paperQuestionRepository.deleteByPaperId(id);
        
        // 删除试卷
        examPaperRepository.deleteById(id);
    }

    @Override
    public ExamPaperResponse getExamPaperById(Long id) {
        ExamPaper examPaper = examPaperRepository.selectById(id);
        if (examPaper == null) {
            throw new RuntimeException("试卷不存在");
        }
        return convertToResponse(examPaper);
    }

    @Override
    public IPage<ExamPaperResponse> getExamPapers(int page, int size, Long examId, ExamPaper.PaperStatus status, String keyword) {
        Page<ExamPaper> pageRequest = new Page<>(page, size);
        QueryWrapper<ExamPaper> queryWrapper = new QueryWrapper<>();

        if (examId != null) {
            queryWrapper.eq("exam_id", examId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("title", keyword);
        }

        queryWrapper.orderByDesc("created_at");
        IPage<ExamPaper> paperPage = examPaperRepository.selectPage(pageRequest, queryWrapper);

        IPage<ExamPaperResponse> responsePage = new Page<>(page, size, paperPage.getTotal());
        List<ExamPaperResponse> responseList = paperPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public List<ExamPaperResponse> getExamPapersByExam(Long examId) {
        List<ExamPaper> papers = examPaperRepository.findByExamId(examId);
        return papers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamPaperResponse> getExamPapersByCreator(Long createdById) {
        List<ExamPaper> papers = examPaperRepository.findByCreatedById(createdById);
        return papers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamPaperResponse publishExamPaper(Long id) {
        ExamPaper examPaper = examPaperRepository.selectById(id);
        if (examPaper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 验证权限
        if (!examPaper.getCreatedById().equals(getCurrentUserId())) {
            throw new RuntimeException("无权限发布此试卷");
        }

        // 验证试卷状态
        if (examPaper.getStatus() != ExamPaper.PaperStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的试卷可以发布");
        }

        // 验证试卷是否有题目
        List<PaperQuestion> questions = paperQuestionRepository.findByPaperId(id);
        if (questions.isEmpty()) {
            throw new RuntimeException("试卷没有题目，无法发布");
        }

        examPaper.setStatus(ExamPaper.PaperStatus.PUBLISHED);
        examPaperRepository.updateById(examPaper);
        return convertToResponse(examPaper);
    }

    @Override
    public ExamPaperResponse archiveExamPaper(Long id) {
        ExamPaper examPaper = examPaperRepository.selectById(id);
        if (examPaper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 验证权限
        if (!examPaper.getCreatedById().equals(getCurrentUserId())) {
            throw new RuntimeException("无权限归档此试卷");
        }

        examPaper.setStatus(ExamPaper.PaperStatus.ARCHIVED);
        examPaperRepository.updateById(examPaper);
        return convertToResponse(examPaper);
    }

    @Override
    public ExamPaperResponse copyExamPaper(Long id) {
        ExamPaper originalPaper = examPaperRepository.selectById(id);
        if (originalPaper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 复制试卷
        ExamPaper newPaper = ExamPaper.builder()
                .title(originalPaper.getTitle() + " - 副本")
                .examId(originalPaper.getExamId())
                .duration(originalPaper.getDuration())
                .totalQuestions(originalPaper.getTotalQuestions())
                .totalScore(originalPaper.getTotalScore())
                .status(ExamPaper.PaperStatus.DRAFT)
                .createdById(getCurrentUserId())
                .build();

        examPaperRepository.insert(newPaper);

        // 复制试卷题目
        List<PaperQuestion> originalQuestions = paperQuestionRepository.findByPaperId(id);
        for (PaperQuestion originalQuestion : originalQuestions) {
            PaperQuestion newQuestion = PaperQuestion.builder()
                    .paperId(newPaper.getId())
                    .questionId(originalQuestion.getQuestionId())
                    .score(originalQuestion.getScore())
                    .sortOrder(originalQuestion.getSortOrder())
                    .build();
            paperQuestionRepository.insert(newQuestion);
        }

        return convertToResponse(newPaper);
    }

    @Override
    public ExamPaperResponse previewExamPaper(Long id) {
        ExamPaper examPaper = examPaperRepository.selectById(id);
        if (examPaper == null) {
            throw new RuntimeException("试卷不存在");
        }

        ExamPaperResponse response = convertToResponse(examPaper);
        
        // 加载试卷题目详情
        List<PaperQuestion> paperQuestions = paperQuestionRepository.findByPaperId(id);
        List<ExamPaperResponse.PaperQuestionInfo> questionInfos = new ArrayList<>();

        for (PaperQuestion paperQuestion : paperQuestions) {
            Question question = questionRepository.selectById(paperQuestion.getQuestionId());
            if (question != null) {
                ExamPaperResponse.PaperQuestionInfo questionInfo = ExamPaperResponse.PaperQuestionInfo.builder()
                        .questionId(question.getId())
                        .questionTitle(question.getTitle())
                        .questionContent(question.getContent())
                        .questionType(question.getType().toString())
                        .difficulty(question.getDifficulty())
                        .score(paperQuestion.getScore())
                        .sortOrder(paperQuestion.getSortOrder())
                        .options(question.getOptions())
                        .build();
                questionInfos.add(questionInfo);
            }
        }

        response.setQuestions(questionInfos);
        return response;
    }

    @Override
    public void addQuestionToPaper(Long paperId, Long questionId, Integer score, Integer sortOrder) {
        // 检查试卷是否存在
        ExamPaper paper = examPaperRepository.selectById(paperId);
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 检查题目是否存在
        Question question = questionRepository.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 检查题目是否已在试卷中
        QueryWrapper<PaperQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("paper_id", paperId).eq("question_id", questionId);
        if (paperQuestionRepository.selectOne(queryWrapper) != null) {
            throw new RuntimeException("题目已在试卷中");
        }

        PaperQuestion paperQuestion = PaperQuestion.builder()
                .paperId(paperId)
                .questionId(questionId)
                .score(score)
                .sortOrder(sortOrder)
                .build();

        paperQuestionRepository.insert(paperQuestion);

        // 更新试卷统计信息
        updatePaperStatistics(paperId);
    }

    @Override
    public void removeQuestionFromPaper(Long paperId, Long questionId) {
        QueryWrapper<PaperQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("paper_id", paperId).eq("question_id", questionId);
        paperQuestionRepository.delete(queryWrapper);

        // 更新试卷统计信息
        updatePaperStatistics(paperId);
    }

    @Override
    public void batchAddQuestionsToPaper(Long paperId, List<Long> questionIds) {
        for (int i = 0; i < questionIds.size(); i++) {
            Long questionId = questionIds.get(i);
            Question question = questionRepository.selectById(questionId);
            if (question != null) {
                addQuestionToPaper(paperId, questionId, question.getScore(), i + 1);
            }
        }
    }

    @Override
    public void updateQuestionOrder(Long paperId, List<Long> questionIds) {
        for (int i = 0; i < questionIds.size(); i++) {
            Long questionId = questionIds.get(i);
            QueryWrapper<PaperQuestion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("paper_id", paperId).eq("question_id", questionId);
            
            PaperQuestion paperQuestion = paperQuestionRepository.selectOne(queryWrapper);
            if (paperQuestion != null) {
                paperQuestion.setSortOrder(i + 1);
                paperQuestionRepository.updateById(paperQuestion);
            }
        }
    }

    @Override
    public Object getPaperStatistics(Long paperId) {
        ExamPaper paper = examPaperRepository.selectById(paperId);
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }

        List<PaperQuestion> questions = paperQuestionRepository.findByPaperId(paperId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("paperId", paperId);
        statistics.put("title", paper.getTitle());
        statistics.put("totalQuestions", questions.size());
        statistics.put("totalScore", questions.stream().mapToInt(PaperQuestion::getScore).sum());
        
        // 按题型统计
        Map<String, Long> typeCount = new HashMap<>();
        Map<String, Integer> typeScore = new HashMap<>();
        
        for (PaperQuestion pq : questions) {
            Question question = questionRepository.selectById(pq.getQuestionId());
            if (question != null) {
                String type = question.getType().toString();
                typeCount.put(type, typeCount.getOrDefault(type, 0L) + 1);
                typeScore.put(type, typeScore.getOrDefault(type, 0) + pq.getScore());
            }
        }
        
        statistics.put("questionTypeCount", typeCount);
        statistics.put("questionTypeScore", typeScore);

        return statistics;
    }

    /**
     * 根据规则选择题目
     */
    private List<Question> selectQuestionsByRule(ExamPaperRequest.QuestionSelectionRule rule) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        
        if (rule.getCategoryId() != null) {
            queryWrapper.eq("category_id", rule.getCategoryId());
        }
        if (rule.getQuestionType() != null) {
            queryWrapper.eq("type", rule.getQuestionType());
        }
        if (rule.getDifficulty() != null) {
            queryWrapper.eq("difficulty", rule.getDifficulty());
        }
        
        queryWrapper.orderByFunc(true, "RAND()"); // 随机排序
        queryWrapper.last("LIMIT " + rule.getCount());
        
        return questionRepository.selectList(queryWrapper);
    }

    /**
     * 更新试卷统计信息
     */
    private void updatePaperStatistics(Long paperId) {
        List<PaperQuestion> questions = paperQuestionRepository.findByPaperId(paperId);
        int totalQuestions = questions.size();
        int totalScore = questions.stream().mapToInt(PaperQuestion::getScore).sum();

        ExamPaper paper = examPaperRepository.selectById(paperId);
        paper.setTotalQuestions(totalQuestions);
        paper.setTotalScore(totalScore);
        examPaperRepository.updateById(paper);
    }

    /**
     * 转换为响应DTO
     */
    private ExamPaperResponse convertToResponse(ExamPaper examPaper) {
        ExamPaperResponse response = new ExamPaperResponse();
        BeanUtils.copyProperties(examPaper, response);
        
        // TODO: 设置关联信息
        // response.setExamTitle(getExamTitle(examPaper.getExamId()));
        // response.setCreatedByName(getUserName(examPaper.getCreatedById()));
        
        return response;
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return 1L; // 临时固定值
    }
}
