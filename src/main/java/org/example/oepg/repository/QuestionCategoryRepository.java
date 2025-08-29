package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.oepg.entity.QuestionCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目分类数据访问接口
 */
@Mapper
public interface QuestionCategoryRepository extends BaseMapper<QuestionCategory> {
    
    /**
     * 根据父分类ID查找子分类
     */
    @Select("SELECT * FROM question_categories WHERE parent_id = #{parentId} ORDER BY sort_order, id")
    List<QuestionCategory> findByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查找所有顶级分类
     */
    @Select("SELECT * FROM question_categories WHERE (parent_id IS NULL OR parent_id = 0) ORDER BY sort_order, id")
    List<QuestionCategory> findTopLevelCategories();
    
    /**
     * 检查分类名称是否存在（同级分类中）
     */
    @Select("SELECT COUNT(*) FROM question_categories WHERE name = #{name} AND (parent_id = #{parentId} OR (parent_id IS NULL AND #{parentId} IS NULL)) AND id != #{excludeId}")
    boolean existsByNameAndParentId(@Param("name") String name, @Param("parentId") Long parentId, @Param("excludeId") Long excludeId);
    
    /**
     * 根据分类ID查找所有子分类ID（递归）
     */
    @Select("WITH RECURSIVE category_tree AS (" +
            "SELECT id, parent_id FROM question_categories WHERE id = #{categoryId} " +
            "UNION ALL " +
            "SELECT c.id, c.parent_id FROM question_categories c " +
            "INNER JOIN category_tree ct ON c.parent_id = ct.id" +
            ") SELECT id FROM category_tree")
    List<Long> findAllChildIds(@Param("categoryId") Long categoryId);
} 