package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.oepg.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目数据访问接口
 */
@Mapper
public interface QuestionRepository extends BaseMapper<Question> {
    
    /**
     * 分页查询题目（带分类名称）
     */
    @Select("SELECT q.*, c.name as category_name, u.real_name as created_by_name " +
            "FROM questions q " +
            "LEFT JOIN question_categories c ON q.category_id = c.id " +
            "LEFT JOIN users u ON q.created_by = u.id " +
            "WHERE (#{categoryId} IS NULL OR q.category_id = #{categoryId}) " +
            "AND (#{type} IS NULL OR q.type = #{type}) " +
            "AND (#{difficulty} IS NULL OR q.difficulty = #{difficulty}) " +
            "AND (#{keyword} IS NULL OR q.title LIKE CONCAT('%', #{keyword}, '%') OR q.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY q.created_at DESC")
    IPage<Question> findQuestionsWithCategory(Page<Question> page, 
                                             @Param("categoryId") Long categoryId,
                                             @Param("type") Question.QuestionType type,
                                             @Param("difficulty") Integer difficulty,
                                             @Param("keyword") String keyword);
    
    /**
     * 根据分类ID查找题目数量
     */
    @Select("SELECT COUNT(*) FROM questions WHERE category_id = #{categoryId}")
    int countByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据创建者ID查找题目
     */
    @Select("SELECT * FROM questions WHERE created_by = #{createdById} ORDER BY created_at DESC")
    List<Question> findByCreatedById(@Param("createdById") Long createdById);
} 