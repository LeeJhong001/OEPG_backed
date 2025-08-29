package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.oepg.dto.req.CategoryRequest;
import org.example.oepg.dto.res.CategoryResponse;
import org.example.oepg.entity.QuestionCategory;
import org.example.oepg.entity.User;
import org.example.oepg.repository.QuestionCategoryRepository;
import org.example.oepg.repository.QuestionRepository;
import org.example.oepg.repository.UserRepository;
import org.example.oepg.service.QuestionCategoryService;
import org.example.oepg.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目分类服务实现类
 */
@Service
@Slf4j
public class QuestionCategoryServiceImpl implements QuestionCategoryService {

    @Autowired
    private QuestionCategoryRepository categoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("=== 开始创建分类 ===");
        log.info("分类请求参数: name={}, parentId={}, description={}", 
                request.getName(), request.getParentId(), request.getDescription());
        
        try {
            // 权限检查：只有教师和管理员可以创建分类
            if (!SecurityUtil.isTeacherOrAdmin()) {
                log.error("权限不足：用户不是教师或管理员");
                throw new RuntimeException("权限不足：只有教师和管理员可以创建分类");
            }

            // 从认证信息中获取用户ID
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                log.error("用户未认证");
                throw new RuntimeException("用户未认证");
            }
            
            User user = userRepository.findByUsername(username);
            if (user == null) {
                log.error("用户不存在: {}", username);
                throw new RuntimeException("用户不存在");
            }

            // 检查分类名称是否已存在（同级分类中）
            if (categoryRepository.existsByNameAndParentId(request.getName(), request.getParentId(), 0L)) {
                log.error("同级分类中已存在相同名称的分类: name={}, parentId={}", request.getName(), request.getParentId());
                throw new RuntimeException("同级分类中已存在相同名称的分类");
            }

            QuestionCategory category = QuestionCategory.builder()
                    .name(request.getName())
                    .parentId(request.getParentId())
                    .description(request.getDescription())
                    .sortOrder(request.getSortOrder())
                    .enabled(request.getEnabled())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            log.info("准备插入分类: {}", category);
            categoryRepository.insert(category);
            log.info("分类创建成功: id={}", category.getId());
            
            return CategoryResponse.fromEntity(category);
        } catch (Exception e) {
            log.error("创建分类失败: ", e);
            throw e;
        }
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("=== 开始更新分类 ===");
        log.info("分类ID: {}, 更新参数: name={}, parentId={}", id, request.getName(), request.getParentId());
        
        try {
            QuestionCategory category = categoryRepository.selectById(id);
            if (category == null) {
                log.error("分类不存在: id={}", id);
                throw new RuntimeException("分类不存在");
            }

            // 权限检查：只有管理员可以编辑所有分类，教师只能编辑自己创建的分类
            if (!SecurityUtil.isAdmin()) {
                if (!SecurityUtil.isTeacher()) {
                    log.error("权限不足：用户不是教师或管理员");
                    throw new RuntimeException("权限不足：只有教师和管理员可以编辑分类");
                }
                // 这里可以添加创建者检查，如果分类有创建者字段的话
                // 目前暂时允许教师编辑所有分类
            }

            // 检查分类名称是否已存在（同级分类中，排除自己）
            if (categoryRepository.existsByNameAndParentId(request.getName(), request.getParentId(), id)) {
                log.error("同级分类中已存在相同名称的分类: name={}, parentId={}, excludeId={}", 
                        request.getName(), request.getParentId(), id);
                throw new RuntimeException("同级分类中已存在相同名称的分类");
            }

            category.setName(request.getName());
            category.setParentId(request.getParentId());
            category.setDescription(request.getDescription());
            category.setSortOrder(request.getSortOrder());
            category.setEnabled(request.getEnabled());
            category.setUpdatedAt(LocalDateTime.now());

            log.info("准备更新分类: {}", category);
            categoryRepository.updateById(category);
            log.info("分类更新成功: id={}", category.getId());
            
            return CategoryResponse.fromEntity(category);
        } catch (Exception e) {
            log.error("更新分类失败: ", e);
            throw e;
        }
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("=== 开始删除分类 ===");
        log.info("分类ID: {}", id);
        
        try {
            // 权限检查：只有管理员可以删除分类
            if (!SecurityUtil.isAdmin()) {
                log.error("权限不足：用户不是管理员");
                throw new RuntimeException("权限不足：只有管理员可以删除分类");
            }

            if (!canDeleteCategory(id)) {
                log.error("分类无法删除: id={}", id);
                throw new RuntimeException("分类下存在题目或子分类，无法删除");
            }

            log.info("准备删除分类: id={}", id);
            categoryRepository.deleteById(id);
            log.info("分类删除成功: id={}", id);
        } catch (Exception e) {
            log.error("删除分类失败: ", e);
            throw e;
        }
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        log.info("=== 获取分类详情 ===");
        log.info("分类ID: {}", id);
        
        try {
            // 权限检查：教师和管理员可以查看分类
            if (!SecurityUtil.isTeacherOrAdmin()) {
                log.error("权限不足：用户不是教师或管理员");
                throw new RuntimeException("权限不足：只有教师和管理员可以查看分类");
            }

            QuestionCategory category = categoryRepository.selectById(id);
            if (category == null) {
                log.error("分类不存在: id={}", id);
                throw new RuntimeException("分类不存在");
            }
            
            log.info("分类获取成功: id={}, name={}", category.getId(), category.getName());
            return CategoryResponse.fromEntity(category);
        } catch (Exception e) {
            log.error("获取分类详情失败: ", e);
            throw e;
        }
    }

    @Override
    public List<CategoryResponse> getAllCategoriesAsTree() {
        log.info("=== 获取所有分类（树形结构） ===");
        
        try {
            // 权限检查：教师和管理员可以查看分类
            if (!SecurityUtil.isTeacherOrAdmin()) {
                log.error("权限不足：用户不是教师或管理员");
                throw new RuntimeException("权限不足：只有教师和管理员可以查看分类");
            }

            log.info("1. 查询所有分类（包括禁用的）...");
            // 获取所有分类，包括禁用的
            QueryWrapper<QuestionCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByAsc("sort_order", "id");
            List<QuestionCategory> allCategories = categoryRepository.selectList(queryWrapper);
            log.info("2. 查询到 {} 个分类", allCategories.size());

            // 构建树形结构
            log.info("3. 开始构建树形结构...");
            List<CategoryResponse> tree = buildCategoryTree(allCategories, null, 0);
            log.info("4. 树形结构构建完成，共 {} 个顶级分类", tree.size());
            
            return tree;
        } catch (Exception e) {
            log.error("获取分类树失败: ", e);
            throw new RuntimeException("获取分类树失败", e);
        }
    }

    @Override
    public List<CategoryResponse> getEnabledCategoriesAsTree() {
        log.info("=== 获取所有启用的分类（树形结构） ===");
        
        try {
            // 权限检查：教师和管理员可以查看分类
            if (!SecurityUtil.isTeacherOrAdmin()) {
                log.error("权限不足：用户不是教师或管理员");
                throw new RuntimeException("权限不足：只有教师和管理员可以查看分类");
            }

            log.info("1. 查询所有启用的分类...");
            // 获取所有启用的分类
            QueryWrapper<QuestionCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("enabled", true).orderByAsc("sort_order", "id");
            List<QuestionCategory> enabledCategories = categoryRepository.selectList(queryWrapper);
            log.info("2. 查询到 {} 个启用的分类", enabledCategories.size());

            // 构建树形结构
            log.info("3. 开始构建树形结构...");
            List<CategoryResponse> tree = buildCategoryTree(enabledCategories, null, 0);
            log.info("4. 树形结构构建完成，共 {} 个顶级分类", tree.size());
            
            return tree;
        } catch (Exception e) {
            log.error("获取启用分类树失败: ", e);
            throw new RuntimeException("获取启用分类树失败", e);
        }
    }

    @Override
    public List<CategoryResponse> getCategoriesByParentId(Long parentId) {
        log.info("=== 根据父分类ID获取子分类 ===");
        log.info("父分类ID: {}", parentId);
        
        try {
            // 权限检查：教师和管理员可以查看分类
            if (!SecurityUtil.isTeacherOrAdmin()) {
                log.error("权限不足：用户不是教师或管理员");
                throw new RuntimeException("权限不足：只有教师和管理员可以查看分类");
            }

            List<QuestionCategory> categories = categoryRepository.findByParentId(parentId);
            log.info("查询到 {} 个子分类", categories.size());
            
            List<CategoryResponse> responses = categories.stream()
                    .map(CategoryResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return responses;
        } catch (Exception e) {
            log.error("获取子分类失败: ", e);
            throw new RuntimeException("获取子分类失败", e);
        }
    }

    @Override
    public List<CategoryResponse> getTopLevelCategories() {
        log.info("=== 获取顶级分类 ===");
        
        try {
            // 权限检查：教师和管理员可以查看分类
            if (!SecurityUtil.isTeacherOrAdmin()) {
                log.error("权限不足：用户不是教师或管理员");
                throw new RuntimeException("权限不足：只有教师和管理员可以查看分类");
            }

            log.info("1. 查询顶级分类（包括禁用的）...");
            List<QuestionCategory> categories = categoryRepository.findTopLevelCategories();
            log.info("2. 查询到 {} 个顶级分类", categories.size());
            
            List<CategoryResponse> responses = categories.stream()
                    .map(CategoryResponse::fromEntity)
                    .collect(Collectors.toList());
            
            log.info("3. 顶级分类获取成功");
            return responses;
        } catch (Exception e) {
            log.error("获取顶级分类失败: ", e);
            throw new RuntimeException("获取顶级分类失败", e);
        }
    }

    @Override
    public boolean canDeleteCategory(Long id) {
        log.info("=== 检查分类是否可以删除 ===");
        log.info("分类ID: {}", id);
        
        try {
            // 权限检查：只有管理员可以检查删除权限
            if (!SecurityUtil.isAdmin()) {
                log.warn("权限不足：用户不是管理员，无法检查删除权限");
                return false;
            }

            // 检查是否有子分类
            List<QuestionCategory> children = categoryRepository.findByParentId(id);
            if (!children.isEmpty()) {
                log.warn("分类有子分类，无法删除: id={}, 子分类数量={}", id, children.size());
                return false;
            }

            // 检查是否有题目
            int questionCount = questionRepository.countByCategoryId(id);
            if (questionCount > 0) {
                log.warn("分类下有题目，无法删除: id={}, 题目数量={}", id, questionCount);
                return false;
            }
            
            log.info("分类可以删除: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("检查分类删除权限失败: ", e);
            return false;
        }
    }

    /**
     * 构建分类树形结构
     */
    private List<CategoryResponse> buildCategoryTree(List<QuestionCategory> allCategories, Long parentId, int level) {
        log.debug("构建分类树: parentId={}, level={}, 总分类数={}", parentId, level, allCategories.size());
        
        List<CategoryResponse> tree = new ArrayList<>();

        for (QuestionCategory category : allCategories) {
            if ((parentId == null && category.getParentId() == null) ||
                (parentId != null && parentId.equals(category.getParentId()))) {
                
                CategoryResponse response = CategoryResponse.fromEntity(category);
                response.setLevel(level);
                
                // 递归构建子分类
                List<CategoryResponse> children = buildCategoryTree(allCategories, category.getId(), level + 1);
                response.setChildren(children);
                response.setHasChildren(!children.isEmpty());
                
                tree.add(response);
                log.debug("添加分类到树: id={}, name={}, level={}, 子分类数={}", 
                        category.getId(), category.getName(), level, children.size());
            }
        }

        return tree;
    }
} 