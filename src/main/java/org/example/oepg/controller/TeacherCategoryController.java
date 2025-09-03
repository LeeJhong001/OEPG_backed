package org.example.oepg.controller;

import org.example.oepg.dto.req.CategoryRequest;
import org.example.oepg.dto.res.CategoryResponse;
import org.example.oepg.service.QuestionCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师端分类管理控制器
 * 提供更丰富的分类管理功能
 */
@RestController
@RequestMapping("/api/teacher/categories")
@CrossOrigin(origins = "*")
public class TeacherCategoryController {

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
     * 根据ID获取分类详情
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
     * 获取分类树（用于教师管理界面）
     */
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategoriesAsTree();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取启用的分类树（用于题目分类选择）
     */
    @GetMapping("/tree/enabled")
    public ResponseEntity<List<CategoryResponse>> getEnabledCategoryTree() {
        try {
            List<CategoryResponse> categories = categoryService.getEnabledCategoriesAsTree();
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
     * 根据父分类获取子分类
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByParent(@PathVariable Long parentId) {
        try {
            List<CategoryResponse> categories = categoryService.getCategoriesByParentId(parentId);
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

    /**
     * 批量启用/禁用分类
     */
    @PutMapping("/batch/status")
    public ResponseEntity<Void> batchUpdateCategoryStatus(@RequestBody List<Long> ids,
                                                         @RequestParam boolean enabled) {
        try {
            for (Long id : ids) {
                CategoryResponse category = categoryService.getCategoryById(id);
                if (category != null) {
                    CategoryRequest request = new CategoryRequest();
                    request.setName(category.getName());
                    request.setParentId(category.getParentId());
                    request.setDescription(category.getDescription());
                    request.setSortOrder(category.getSortOrder());
                    request.setEnabled(enabled);
                    categoryService.updateCategory(id, request);
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 移动分类到新的父分类下
     */
    @PutMapping("/{id}/move")
    public ResponseEntity<CategoryResponse> moveCategory(@PathVariable Long id,
                                                       @RequestParam(required = false) Long newParentId) {
        try {
            CategoryResponse category = categoryService.getCategoryById(id);
            if (category == null) {
                return ResponseEntity.notFound().build();
            }

            CategoryRequest request = new CategoryRequest();
            request.setName(category.getName());
            request.setParentId(newParentId);
            request.setDescription(category.getDescription());
            request.setSortOrder(category.getSortOrder());
            request.setEnabled(category.getEnabled());

            CategoryResponse response = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 批量更新分类排序
     */
    @PutMapping("/batch/sort")
    public ResponseEntity<Void> batchUpdateCategorySort(@RequestBody Map<Long, Integer> sortMap) {
        try {
            for (Map.Entry<Long, Integer> entry : sortMap.entrySet()) {
                Long id = entry.getKey();
                Integer sortOrder = entry.getValue();
                
                CategoryResponse category = categoryService.getCategoryById(id);
                if (category != null) {
                    CategoryRequest request = new CategoryRequest();
                    request.setName(category.getName());
                    request.setParentId(category.getParentId());
                    request.setDescription(category.getDescription());
                    request.setSortOrder(sortOrder);
                    request.setEnabled(category.getEnabled());
                    categoryService.updateCategory(id, request);
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取分类统计信息
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Object> getCategoryStatistics(@PathVariable Long id) {
        try {
            // TODO: 实现分类统计逻辑
            // 统计该分类下的题目数量、难度分布等
            Map<String, Object> response = new HashMap<>();
            response.put("categoryId", id);
            response.put("totalQuestions", 0);
            response.put("easyQuestions", 0);
            response.put("mediumQuestions", 0);
            response.put("hardQuestions", 0);
            response.put("questionTypes", new HashMap<>());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 搜索分类
     */
    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> searchCategories(@RequestParam String keyword) {
        try {
            // TODO: 实现分类搜索逻辑
            List<CategoryResponse> allCategories = categoryService.getAllCategoriesAsTree();
            List<CategoryResponse> filteredCategories = allCategories.stream()
                .filter(category -> category.getName().contains(keyword) || 
                               (category.getDescription() != null && category.getDescription().contains(keyword)))
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(filteredCategories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
