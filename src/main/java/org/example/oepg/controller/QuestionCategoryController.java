package org.example.oepg.controller;

import org.example.oepg.dto.req.CategoryRequest;
import org.example.oepg.dto.res.CategoryResponse;
import org.example.oepg.service.QuestionCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 题目分类控制器
 */
@RestController
@RequestMapping("/api/question-categories")
@CrossOrigin(origins = "*")
public class QuestionCategoryController {

    @Autowired
    private QuestionCategoryService categoryService;

    /**
     * 创建分类
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        try {
            CategoryResponse response = categoryService.createCategory(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                         @Valid @RequestBody CategoryRequest request) {
        try {
            CategoryResponse response = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        try {
            CategoryResponse response = categoryService.getCategoryById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取所有分类（树形结构）
     */
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryResponse>> getAllCategoriesAsTree() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategoriesAsTree();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取所有启用的分类（树形结构，用于前端显示）
     */
    @GetMapping("/tree/enabled")
    public ResponseEntity<List<CategoryResponse>> getEnabledCategoriesAsTree() {
        try {
            List<CategoryResponse> categories = categoryService.getEnabledCategoriesAsTree();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取所有分类（包括禁用的，用于管理界面）
     */
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategoriesAsTree();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根据父分类ID获取子分类
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByParentId(@PathVariable Long parentId) {
        try {
            List<CategoryResponse> categories = categoryService.getCategoriesByParentId(parentId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取顶级分类
     */
    @GetMapping("/top-level")
    public ResponseEntity<List<CategoryResponse>> getTopLevelCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getTopLevelCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 检查分类是否可以删除
     */
    @GetMapping("/{id}/can-delete")
    public ResponseEntity<Boolean> canDeleteCategory(@PathVariable Long id) {
        try {
            boolean canDelete = categoryService.canDeleteCategory(id);
            return ResponseEntity.ok(canDelete);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 