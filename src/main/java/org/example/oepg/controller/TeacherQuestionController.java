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
import java.util.Map;

/**
 * 教师端题库管理控制器
 * 提供更丰富的题库管理功能
 */
@RestController
@RequestMapping("/api/teacher/questions")
@CrossOrigin(origins = "*")
public class TeacherQuestionController {

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
     * 根据ID获取题目详情
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
     * 获取我创建的题目
     */
    @GetMapping("/my-questions")
    public ResponseEntity<List<QuestionResponse>> getMyQuestions() {
        try {
            Long currentUserId = 1L; // TODO: 从安全上下文获取
            List<QuestionResponse> questions = questionService.getQuestionsByCreator(currentUserId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据分类获取题目
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
     * 复制题目
     */
    @PostMapping("/{id}/copy")
    public ResponseEntity<QuestionResponse> copyQuestion(@PathVariable Long id) {
        try {
            QuestionResponse response = questionService.copyQuestion(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
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

    /**
     * 批量更新题目分类
     */
    @PutMapping("/batch/category")
    public ResponseEntity<Void> batchUpdateQuestionCategory(@RequestBody List<Long> ids,
                                                           @RequestParam Long categoryId) {
        try {
            questionService.batchUpdateQuestionCategory(ids, categoryId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 批量更新题目难度
     */
    @PutMapping("/batch/difficulty")
    public ResponseEntity<Void> batchUpdateQuestionDifficulty(@RequestBody List<Long> ids,
                                                             @RequestParam Integer difficulty) {
        try {
            for (Long id : ids) {
                QuestionResponse question = questionService.getQuestionById(id);
                if (question != null) {
                    QuestionRequest request = new QuestionRequest();
                    request.setTitle(question.getTitle());
                    request.setContent(question.getContent());
                    request.setType(Question.QuestionType.valueOf(question.getType()));
                    request.setDifficulty(difficulty);
                    request.setCategoryId(question.getCategoryId());
                    request.setAnswer(question.getAnswer());
                    request.setAnalysis(question.getAnalysis());
                    request.setOptions(question.getOptions());
                    request.setScore(question.getScore());
                    questionService.updateQuestion(id, request);
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 随机获取题目（用于组卷）
     */
    @GetMapping("/random")
    public ResponseEntity<List<QuestionResponse>> getRandomQuestions(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Question.QuestionType type,
            @RequestParam(required = false) Integer difficulty) {
        try {
            List<QuestionResponse> questions = questionService.getRandomQuestions(count, categoryId, type, difficulty);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据难度分布获取题目
     */
    @GetMapping("/difficulty-distribution")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByDifficultyDistribution(
            @RequestParam Long categoryId,
            @RequestParam int easy,
            @RequestParam int medium,
            @RequestParam int hard) {
        try {
            List<QuestionResponse> questions = questionService.getQuestionsByDifficultyDistribution(
                categoryId, easy, medium, hard);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取题目统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getQuestionStatistics() {
        try {
            Object statistics = questionService.getQuestionStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 搜索建议（自动补全）
     */
    @GetMapping("/search-suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(@RequestParam String keyword) {
        try {
            List<String> suggestions = questionService.getSearchSuggestions(keyword);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 验证题目答案
     */
    @PostMapping("/{id}/validate")
    public ResponseEntity<Object> validateAnswer(@PathVariable Long id,
                                                @RequestParam String userAnswer) {
        try {
            Object result = questionService.validateAnswer(id, userAnswer);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取题目使用记录
     */
    @GetMapping("/{id}/usage-history")
    public ResponseEntity<Object> getQuestionUsageHistory(@PathVariable Long id) {
        try {
            Object history = questionService.getQuestionUsageHistory(id);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 导入题目（批量）
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importQuestions(@RequestBody List<QuestionRequest> questions) {
        try {
            int successCount = 0;
            int failCount = 0;
            List<String> errors = new java.util.ArrayList<>();

            for (QuestionRequest request : questions) {
                try {
                    questionService.createQuestion(request);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errors.add("题目 '" + request.getTitle() + "' 导入失败: " + e.getMessage());
                }
            }

            Map<String, Object> result = Map.of(
                "total", questions.size(),
                "success", successCount,
                "failed", failCount,
                "errors", errors
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 导出题目
     */
    @GetMapping("/export")
    public ResponseEntity<List<QuestionResponse>> exportQuestions(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Question.QuestionType type,
            @RequestParam(required = false) Integer difficulty) {
        try {
            IPage<QuestionResponse> questions = questionService.getQuestions(1, Integer.MAX_VALUE, categoryId, type, difficulty, null);
            return ResponseEntity.ok(questions.getRecords());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 题目收藏/取消收藏
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleQuestionFavorite(@PathVariable Long id) {
        try {
            // TODO: 实现题目收藏功能
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取收藏的题目
     */
    @GetMapping("/favorites")
    public ResponseEntity<List<QuestionResponse>> getFavoriteQuestions() {
        try {
            // TODO: 实现获取收藏题目功能
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 题目标签管理
     */
    @PostMapping("/{id}/tags")
    public ResponseEntity<Void> updateQuestionTags(@PathVariable Long id,
                                                   @RequestBody List<String> tags) {
        try {
            // TODO: 实现题目标签功能
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据标签获取题目
     */
    @GetMapping("/tags/{tag}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByTag(@PathVariable String tag) {
        try {
            // TODO: 实现根据标签获取题目功能
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
