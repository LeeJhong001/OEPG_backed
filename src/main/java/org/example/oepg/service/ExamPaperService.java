package org.example.oepg.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.oepg.dto.req.ExamPaperRequest;
import org.example.oepg.dto.res.ExamPaperResponse;
import org.example.oepg.entity.ExamPaper;

import java.util.List;

/**
 * 试卷服务接口
 */
public interface ExamPaperService {

    /**
     * 创建试卷
     */
    ExamPaperResponse createExamPaper(ExamPaperRequest request);

    /**
     * 智能组卷
     */
    ExamPaperResponse generatePaperByRules(ExamPaperRequest request);

    /**
     * 更新试卷
     */
    ExamPaperResponse updateExamPaper(Long id, ExamPaperRequest request);

    /**
     * 删除试卷
     */
    void deleteExamPaper(Long id);

    /**
     * 根据ID获取试卷详情
     */
    ExamPaperResponse getExamPaperById(Long id);

    /**
     * 分页查询试卷
     */
    IPage<ExamPaperResponse> getExamPapers(int page, int size, Long examId, ExamPaper.PaperStatus status, String keyword);

    /**
     * 根据考试ID获取试卷列表
     */
    List<ExamPaperResponse> getExamPapersByExam(Long examId);

    /**
     * 根据创建者获取试卷列表
     */
    List<ExamPaperResponse> getExamPapersByCreator(Long createdById);

    /**
     * 发布试卷
     */
    ExamPaperResponse publishExamPaper(Long id);

    /**
     * 归档试卷
     */
    ExamPaperResponse archiveExamPaper(Long id);

    /**
     * 复制试卷
     */
    ExamPaperResponse copyExamPaper(Long id);

    /**
     * 预览试卷
     */
    ExamPaperResponse previewExamPaper(Long id);

    /**
     * 添加题目到试卷
     */
    void addQuestionToPaper(Long paperId, Long questionId, Integer score, Integer sortOrder);

    /**
     * 从试卷移除题目
     */
    void removeQuestionFromPaper(Long paperId, Long questionId);

    /**
     * 批量添加题目到试卷
     */
    void batchAddQuestionsToPaper(Long paperId, List<Long> questionIds);

    /**
     * 更新试卷题目顺序
     */
    void updateQuestionOrder(Long paperId, List<Long> questionIds);

    /**
     * 获取试卷统计信息
     */
    Object getPaperStatistics(Long paperId);
}
