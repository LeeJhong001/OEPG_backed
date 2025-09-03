package org.example.oepg.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.oepg.dto.req.ExamRequest;
import org.example.oepg.dto.res.ExamResponse;
import org.example.oepg.entity.Exam;
import org.example.oepg.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 考试管理控制器 - 教师端
 */
@RestController
@RequestMapping("/api/teacher/exams")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamService examService;

    /**
     * 创建考试
     */
    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest request) {
        try {
            ExamResponse response = examService.createExam(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新考试
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Long id,
                                                 @Valid @RequestBody ExamRequest request) {
        try {
            ExamResponse response = examService.updateExam(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除考试
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        try {
            examService.deleteExam(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据ID获取考试详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id) {
        try {
            ExamResponse response = examService.getExamById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 分页查询考试列表
     */
    @GetMapping
    public ResponseEntity<IPage<ExamResponse>> getExams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Exam.ExamStatus status,
            @RequestParam(required = false) String keyword) {
        try {
            IPage<ExamResponse> exams = examService.getExams(page, size, subjectId, status, keyword);
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取我创建的考试列表
     */
    @GetMapping("/my-exams")
    public ResponseEntity<List<ExamResponse>> getMyExams() {
        try {
            // TODO: 从安全上下文获取当前用户ID
            Long currentUserId = 1L; // 临时固定值
            List<ExamResponse> exams = examService.getExamsByCreator(currentUserId);
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据科目获取考试列表
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ExamResponse>> getExamsBySubject(@PathVariable Long subjectId) {
        try {
            List<ExamResponse> exams = examService.getExamsBySubject(subjectId);
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 发布考试
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<ExamResponse> publishExam(@PathVariable Long id) {
        try {
            ExamResponse response = examService.publishExam(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 归档考试
     */
    @PutMapping("/{id}/archive")
    public ResponseEntity<ExamResponse> archiveExam(@PathVariable Long id) {
        try {
            ExamResponse response = examService.archiveExam(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取正在进行的考试
     */
    @GetMapping("/ongoing")
    public ResponseEntity<List<ExamResponse>> getOngoingExams() {
        try {
            List<ExamResponse> exams = examService.getOngoingExams();
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取即将开始的考试
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<ExamResponse>> getUpcomingExams() {
        try {
            List<ExamResponse> exams = examService.getUpcomingExams();
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 复制考试
     */
    @PostMapping("/{id}/copy")
    public ResponseEntity<ExamResponse> copyExam(@PathVariable Long id) {
        try {
            ExamResponse response = examService.copyExam(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 批量删除考试
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteExams(@RequestBody List<Long> ids) {
        try {
            examService.batchDeleteExams(ids);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取考试统计信息
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Object> getExamStatistics(@PathVariable Long id) {
        try {
            Object statistics = examService.getExamStatistics(id);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
