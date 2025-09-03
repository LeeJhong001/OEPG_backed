package org.example.oepg.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.oepg.dto.req.ExamPaperRequest;
import org.example.oepg.dto.res.ExamPaperResponse;
import org.example.oepg.entity.ExamPaper;
import org.example.oepg.service.ExamPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 试卷管理控制器 - 教师端
 */
@RestController
@RequestMapping("/api/teacher/exam-papers")
@CrossOrigin(origins = "*")
public class ExamPaperController {

    @Autowired
    private ExamPaperService examPaperService;

    /**
     * 创建试卷
     */
    @PostMapping
    public ResponseEntity<ExamPaperResponse> createExamPaper(@Valid @RequestBody ExamPaperRequest request) {
        try {
            ExamPaperResponse response = examPaperService.createExamPaper(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 智能组卷
     */
    @PostMapping("/generate")
    public ResponseEntity<ExamPaperResponse> generatePaperByRules(@Valid @RequestBody ExamPaperRequest request) {
        try {
            ExamPaperResponse response = examPaperService.generatePaperByRules(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新试卷
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamPaperResponse> updateExamPaper(@PathVariable Long id,
                                                           @Valid @RequestBody ExamPaperRequest request) {
        try {
            ExamPaperResponse response = examPaperService.updateExamPaper(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除试卷
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamPaper(@PathVariable Long id) {
        try {
            examPaperService.deleteExamPaper(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据ID获取试卷详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamPaperResponse> getExamPaperById(@PathVariable Long id) {
        try {
            ExamPaperResponse response = examPaperService.getExamPaperById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 分页查询试卷列表
     */
    @GetMapping
    public ResponseEntity<IPage<ExamPaperResponse>> getExamPapers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) ExamPaper.PaperStatus status,
            @RequestParam(required = false) String keyword) {
        try {
            IPage<ExamPaperResponse> papers = examPaperService.getExamPapers(page, size, examId, status, keyword);
            return ResponseEntity.ok(papers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取我创建的试卷列表
     */
    @GetMapping("/my-papers")
    public ResponseEntity<List<ExamPaperResponse>> getMyExamPapers() {
        try {
            Long currentUserId = 1L; // TODO: 从安全上下文获取
            List<ExamPaperResponse> papers = examPaperService.getExamPapersByCreator(currentUserId);
            return ResponseEntity.ok(papers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据考试ID获取试卷列表
     */
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamPaperResponse>> getExamPapersByExam(@PathVariable Long examId) {
        try {
            List<ExamPaperResponse> papers = examPaperService.getExamPapersByExam(examId);
            return ResponseEntity.ok(papers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 发布试卷
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<ExamPaperResponse> publishExamPaper(@PathVariable Long id) {
        try {
            ExamPaperResponse response = examPaperService.publishExamPaper(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 归档试卷
     */
    @PutMapping("/{id}/archive")
    public ResponseEntity<ExamPaperResponse> archiveExamPaper(@PathVariable Long id) {
        try {
            ExamPaperResponse response = examPaperService.archiveExamPaper(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 复制试卷
     */
    @PostMapping("/{id}/copy")
    public ResponseEntity<ExamPaperResponse> copyExamPaper(@PathVariable Long id) {
        try {
            ExamPaperResponse response = examPaperService.copyExamPaper(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 预览试卷
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<ExamPaperResponse> previewExamPaper(@PathVariable Long id) {
        try {
            ExamPaperResponse response = examPaperService.previewExamPaper(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 添加题目到试卷
     */
    @PostMapping("/{paperId}/questions/{questionId}")
    public ResponseEntity<Void> addQuestionToPaper(@PathVariable Long paperId,
                                                  @PathVariable Long questionId,
                                                  @RequestParam Integer score,
                                                  @RequestParam(defaultValue = "1") Integer sortOrder) {
        try {
            examPaperService.addQuestionToPaper(paperId, questionId, score, sortOrder);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 从试卷移除题目
     */
    @DeleteMapping("/{paperId}/questions/{questionId}")
    public ResponseEntity<Void> removeQuestionFromPaper(@PathVariable Long paperId,
                                                       @PathVariable Long questionId) {
        try {
            examPaperService.removeQuestionFromPaper(paperId, questionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 批量添加题目到试卷
     */
    @PostMapping("/{paperId}/questions/batch")
    public ResponseEntity<Void> batchAddQuestionsToPaper(@PathVariable Long paperId,
                                                        @RequestBody List<Long> questionIds) {
        try {
            examPaperService.batchAddQuestionsToPaper(paperId, questionIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新试卷题目顺序
     */
    @PutMapping("/{paperId}/questions/order")
    public ResponseEntity<Void> updateQuestionOrder(@PathVariable Long paperId,
                                                   @RequestBody List<Long> questionIds) {
        try {
            examPaperService.updateQuestionOrder(paperId, questionIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取试卷统计信息
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Object> getPaperStatistics(@PathVariable Long id) {
        try {
            Object statistics = examPaperService.getPaperStatistics(id);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
