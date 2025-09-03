package org.example.oepg.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.oepg.dto.req.ExamRequest;
import org.example.oepg.dto.res.ExamResponse;
import org.example.oepg.entity.Exam;

import java.util.List;

/**
 * 考试服务接口
 */
public interface ExamService {

    /**
     * 创建考试
     */
    ExamResponse createExam(ExamRequest request);

    /**
     * 更新考试
     */
    ExamResponse updateExam(Long id, ExamRequest request);

    /**
     * 删除考试
     */
    void deleteExam(Long id);

    /**
     * 根据ID获取考试
     */
    ExamResponse getExamById(Long id);

    /**
     * 分页查询考试
     */
    IPage<ExamResponse> getExams(int page, int size, Long subjectId, Exam.ExamStatus status, String keyword);

    /**
     * 根据创建者获取考试列表
     */
    List<ExamResponse> getExamsByCreator(Long createdById);

    /**
     * 根据科目获取考试列表
     */
    List<ExamResponse> getExamsBySubject(Long subjectId);

    /**
     * 发布考试
     */
    ExamResponse publishExam(Long id);

    /**
     * 归档考试
     */
    ExamResponse archiveExam(Long id);

    /**
     * 获取正在进行的考试
     */
    List<ExamResponse> getOngoingExams();

    /**
     * 获取即将开始的考试
     */
    List<ExamResponse> getUpcomingExams();

    /**
     * 复制考试
     */
    ExamResponse copyExam(Long id);

    /**
     * 批量删除考试
     */
    void batchDeleteExams(List<Long> ids);

    /**
     * 获取考试统计信息
     */
    Object getExamStatistics(Long examId);
}
