package org.example.oepg.controller;

import org.example.oepg.dto.req.SubmitAnswerRequest;
import org.example.oepg.dto.res.RecordResponse;
import org.example.oepg.dto.res.StartExamResponse;
import org.example.oepg.dto.res.SubmitAnswerResponse;
import org.example.oepg.service.StudentExamService;
import org.example.oepg.repository.UserRepository;
import org.example.oepg.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentExamController {

    @Autowired
    private StudentExamService studentExamService;

    @Autowired
    private UserRepository userRepository;

    // 列出当前学生可参加的考试
    @GetMapping("/exams/available")
    public ResponseEntity<List<StartExamResponse.AvailableExam>> getAvailableExams() {
        try {
            Long studentId = getCurrentUserId();
            return ResponseEntity.ok(studentExamService.getAvailableExams(studentId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 开始考试：返回记录与试卷结构
    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<StartExamResponse> startExam(@PathVariable Long examId) {
        try {
            Long studentId = getCurrentUserId();
            StartExamResponse resp = studentExamService.startExam(examId, studentId);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 提交答案：自动批改客观题
    @PostMapping("/exams/{examId}/submit")
    public ResponseEntity<SubmitAnswerResponse> submitExam(@PathVariable Long examId,
                                                           @Validated @RequestBody SubmitAnswerRequest request) {
        try {
            Long studentId = getCurrentUserId();
            SubmitAnswerResponse resp = studentExamService.submitExam(examId, studentId, request);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 我的考试记录列表
    @GetMapping("/records")
    public ResponseEntity<List<RecordResponse>> myRecords() {
        try {
            Long studentId = getCurrentUserId();
            return ResponseEntity.ok(studentExamService.getMyRecords(studentId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 单次记录详情
    @GetMapping("/records/{recordId}")
    public ResponseEntity<RecordResponse> recordDetail(@PathVariable Long recordId) {
        try {
            Long studentId = getCurrentUserId();
            return ResponseEntity.ok(studentExamService.getRecordDetail(recordId, studentId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("未认证用户");
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在: " + username);
        }
        return user.getId();
    }
}
