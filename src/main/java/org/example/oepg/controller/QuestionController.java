package org.example.oepg.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.oepg.dto.req.QuestionRequest;
import org.example.oepg.dto.res.QuestionResponse;
import org.example.oepg.entity.Question;
import org.example.oepg.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 题目控制器
 */
@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 创建题目
     */
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        try {
            QuestionResponse response = questionService.createQuestion(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新题目
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id,
                                                         @Valid @RequestBody QuestionRequest request) {
        try {
            QuestionResponse response = questionService.updateQuestion(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据ID获取题目
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        try {
            QuestionResponse response = questionService.getQuestionById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 分页查询题目
     */
    @GetMapping
    public ResponseEntity<IPage<QuestionResponse>> getQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Question.QuestionType type,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String keyword) {
        try {
            IPage<QuestionResponse> questions = questionService.getQuestions(page, size, categoryId, type, difficulty, keyword);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据分类ID获取题目
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByCategory(@PathVariable Long categoryId) {
        try {
            List<QuestionResponse> questions = questionService.getQuestionsByCategory(categoryId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据创建者ID获取题目
     */
    @GetMapping("/creator/{createdById}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByCreator(@PathVariable Long createdById) {
        try {
            List<QuestionResponse> questions = questionService.getQuestionsByCreator(createdById);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 批量删除题目
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteQuestions(@RequestBody List<Long> ids) {
        try {
            questionService.batchDeleteQuestions(ids);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 