package org.example.oepg.service;

import org.example.oepg.dto.req.CategoryRequest;
import org.example.oepg.dto.res.CategoryResponse;

import java.util.List;

/**
 * 题目分类服务接口
 */
public interface QuestionCategoryService {
    
    /**
     * 创建分类
     */
    CategoryResponse createCategory(CategoryRequest request);
    
    /**
     * 更新分类
     */
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long id);
    
    /**
     * 根据ID获取分类详情
     */
    CategoryResponse getCategoryById(Long id);
    
    /**
     * 获取所有分类（树形结构，包括禁用的）
     */
    List<CategoryResponse> getAllCategoriesAsTree();
    
    /**
     * 获取所有启用的分类（树形结构，用于前端显示）
     */
    List<CategoryResponse> getEnabledCategoriesAsTree();
    
    /**
     * 根据父分类ID获取子分类
     */
    List<CategoryResponse> getCategoriesByParentId(Long parentId);
    
    /**
     * 获取顶级分类
     */
    List<CategoryResponse> getTopLevelCategories();
    
    /**
     * 检查分类是否可以删除
     */
    boolean canDeleteCategory(Long id);
} 