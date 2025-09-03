package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.oepg.dto.req.ExamRequest;
import org.example.oepg.dto.res.ExamResponse;
import org.example.oepg.entity.Exam;
import org.example.oepg.exception.BusinessException;
import org.example.oepg.repository.ExamRepository;
import org.example.oepg.service.ExamService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试服务实现类
 */
@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Override
    public ExamResponse createExam(ExamRequest request) {
        // 验证时间逻辑
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BusinessException("INVALID_TIME_RANGE", "结束时间不能早于开始时间");
        }

        Exam exam = Exam.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subjectId(request.getSubjectId())
                .duration(request.getDuration())
                .totalScore(request.getTotalScore())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(request.getStatus())
                .createdById(getCurrentUserId()) // 需要从安全上下文获取
                .build();

        examRepository.insert(exam);
        return convertToResponse(exam);
    }

    @Override
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.selectById(id);
        if (exam == null) {
            throw new BusinessException("EXAM_NOT_FOUND", "考试不存在");
        }

        // 验证权限：只有创建者可以修改
        if (!exam.getCreatedById().equals(getCurrentUserId())) {
            throw new BusinessException("PERMISSION_DENIED", "无权限修改此考试");
        }

        // 验证时间逻辑
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BusinessException("INVALID_TIME_RANGE", "结束时间不能早于开始时间");
        }

        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setSubjectId(request.getSubjectId());
        exam.setDuration(request.getDuration());
        exam.setTotalScore(request.getTotalScore());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setStatus(request.getStatus());

        examRepository.updateById(exam);
        return convertToResponse(exam);
    }

    @Override
    public void deleteExam(Long id) {
        Exam exam = examRepository.selectById(id);
        if (exam == null) {
            throw new BusinessException("EXAM_NOT_FOUND", "考试不存在");
        }

        // 验证权限：只有创建者可以删除
        if (!exam.getCreatedById().equals(getCurrentUserId())) {
            throw new BusinessException("PERMISSION_DENIED", "无权限删除此考试");
        }

        // 检查考试状态，已发布的考试不能删除
        if (exam.getStatus() == Exam.ExamStatus.PUBLISHED || 
            exam.getStatus() == Exam.ExamStatus.ONGOING) {
            throw new BusinessException("EXAM_CANNOT_DELETE", "已发布或进行中的考试不能删除");
        }

        examRepository.deleteById(id);
    }

    @Override
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.selectById(id);
        if (exam == null) {
            throw new BusinessException("EXAM_NOT_FOUND", "考试不存在");
        }
        return convertToResponse(exam);
    }

    @Override
    public IPage<ExamResponse> getExams(int page, int size, Long subjectId, Exam.ExamStatus status, String keyword) {
        Page<Exam> pageRequest = new Page<>(page, size);
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();

        if (subjectId != null) {
            queryWrapper.eq("subject_id", subjectId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("title", keyword).or().like("description", keyword);
        }

        queryWrapper.orderByDesc("created_at");
        IPage<Exam> examPage = examRepository.selectPage(pageRequest, queryWrapper);

        // 转换为响应DTO
        IPage<ExamResponse> responsePage = new Page<>(page, size, examPage.getTotal());
        List<ExamResponse> responseList = examPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public List<ExamResponse> getExamsByCreator(Long createdById) {
        List<Exam> exams = examRepository.findByCreatedById(createdById);
        return exams.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponse> getExamsBySubject(Long subjectId) {
        List<Exam> exams = examRepository.findBySubjectId(subjectId);
        return exams.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponse publishExam(Long id) {
        Exam exam = examRepository.selectById(id);
        if (exam == null) {
            throw new BusinessException("EXAM_NOT_FOUND", "考试不存在");
        }

        // 验证权限
        if (!exam.getCreatedById().equals(getCurrentUserId())) {
            throw new BusinessException("PERMISSION_DENIED", "无权限发布此考试");
        }

        // 验证考试状态
        if (exam.getStatus() != Exam.ExamStatus.DRAFT) {
            throw new BusinessException("INVALID_EXAM_STATUS", "只有草稿状态的考试可以发布");
        }

        // 验证考试时间
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("INVALID_START_TIME", "考试开始时间不能早于当前时间");
        }

        exam.setStatus(Exam.ExamStatus.PUBLISHED);
        examRepository.updateById(exam);
        return convertToResponse(exam);
    }

    @Override
    public ExamResponse archiveExam(Long id) {
        Exam exam = examRepository.selectById(id);
        if (exam == null) {
            throw new BusinessException("EXAM_NOT_FOUND", "考试不存在");
        }

        // 验证权限
        if (!exam.getCreatedById().equals(getCurrentUserId())) {
            throw new BusinessException("PERMISSION_DENIED", "无权限归档此考试");
        }

        exam.setStatus(Exam.ExamStatus.ARCHIVED);
        examRepository.updateById(exam);
        return convertToResponse(exam);
    }

    @Override
    public List<ExamResponse> getOngoingExams() {
        List<Exam> exams = examRepository.findOngoingExams();
        return exams.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponse> getUpcomingExams() {
        List<Exam> exams = examRepository.findUpcomingExams();
        return exams.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponse copyExam(Long id) {
        Exam originalExam = examRepository.selectById(id);
        if (originalExam == null) {
            throw new BusinessException("EXAM_NOT_FOUND", "考试不存在");
        }

        Exam newExam = Exam.builder()
                .title(originalExam.getTitle() + " - 副本")
                .description(originalExam.getDescription())
                .subjectId(originalExam.getSubjectId())
                .duration(originalExam.getDuration())
                .totalScore(originalExam.getTotalScore())
                .startTime(originalExam.getStartTime())
                .endTime(originalExam.getEndTime())
                .status(Exam.ExamStatus.DRAFT)
                .createdById(getCurrentUserId())
                .build();

        examRepository.insert(newExam);
        return convertToResponse(newExam);
    }

    @Override
    public void batchDeleteExams(List<Long> ids) {
        for (Long id : ids) {
            deleteExam(id); // 复用单个删除的逻辑和权限验证
        }
    }

    @Override
    public Object getExamStatistics(Long examId) {
        Exam exam = examRepository.selectById(examId);
        if (exam == null) {
            throw new BusinessException("EXAM_NOT_FOUND", "考试不存在");
        }

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("examId", examId);
        statistics.put("title", exam.getTitle());
        statistics.put("status", exam.getStatus());
        statistics.put("totalStudents", 0); // TODO: 从考试记录表统计
        statistics.put("completedCount", 0); // TODO: 从考试记录表统计
        statistics.put("averageScore", 0.0); // TODO: 从考试记录表统计
        statistics.put("passRate", 0.0); // TODO: 计算及格率

        return statistics;
    }

    /**
     * 转换为响应DTO
     */
    private ExamResponse convertToResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        BeanUtils.copyProperties(exam, response);
        
        // TODO: 设置关联信息
        // response.setSubjectName(getSubjectName(exam.getSubjectId()));
        // response.setCreatedByName(getUserName(exam.getCreatedById()));
        
        return response;
    }

    /**
     * 获取当前用户ID
     * TODO: 从Spring Security上下文获取当前用户ID
     */
    private Long getCurrentUserId() {
        // 临时返回固定值，实际应从安全上下文获取
        return 1L;
    }
}
