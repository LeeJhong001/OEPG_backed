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
    
    /**
     * 随机获取题目
     */
    @Select("<script>" +
            "SELECT * FROM questions WHERE 1=1 " +
            "<if test='categoryId != null'> AND category_id = #{categoryId} </if>" +
            "<if test='type != null'> AND type = #{type} </if>" +
            "<if test='difficulty != null'> AND difficulty = #{difficulty} </if>" +
            "ORDER BY RAND() LIMIT #{count}" +
            "</script>")
    List<Question> findRandomQuestions(@Param("count") int count,
                                     @Param("categoryId") Long categoryId,
                                     @Param("type") Question.QuestionType type,
                                     @Param("difficulty") Integer difficulty);
    
    /**
     * 批量更新题目状态
     */
    @Select("UPDATE questions SET enabled = #{enabled}, updated_at = NOW() WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>")
    void batchUpdateStatus(@Param("ids") List<Long> ids, @Param("enabled") boolean enabled);
    
    /**
     * 批量更新题目分类
     */
    @Select("UPDATE questions SET category_id = #{categoryId}, updated_at = NOW() WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>")
    void batchUpdateCategory(@Param("ids") List<Long> ids, @Param("categoryId") Long categoryId);
    
    /**
     * 获取题目统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as totalCount, " +
            "COUNT(CASE WHEN type = 'CHOICE' THEN 1 END) as choiceCount, " +
            "COUNT(CASE WHEN type = 'FILL_BLANK' THEN 1 END) as fillBlankCount, " +
            "COUNT(CASE WHEN type = 'SHORT_ANSWER' THEN 1 END) as shortAnswerCount, " +
            "COUNT(CASE WHEN type = 'PROOF' THEN 1 END) as proofCount, " +
            "COUNT(CASE WHEN difficulty = 1 THEN 1 END) as easyCount, " +
            "COUNT(CASE WHEN difficulty = 2 THEN 1 END) as mediumCount, " +
            "COUNT(CASE WHEN difficulty = 3 THEN 1 END) as hardCount " +
            "FROM questions")
    Object getQuestionStatistics();
    
    /**
     * 根据难度分布获取题目
     */
    @Select("(" +
            "SELECT * FROM questions WHERE category_id = #{categoryId} AND difficulty = 1 ORDER BY RAND() LIMIT #{easy}" +
            ") UNION ALL (" +
            "SELECT * FROM questions WHERE category_id = #{categoryId} AND difficulty = 2 ORDER BY RAND() LIMIT #{medium}" +
            ") UNION ALL (" +
            "SELECT * FROM questions WHERE category_id = #{categoryId} AND difficulty = 3 ORDER BY RAND() LIMIT #{hard}" +
            ")")
    List<Question> findQuestionsByDifficultyDistribution(@Param("categoryId") Long categoryId,
                                                        @Param("easy") int easy,
                                                        @Param("medium") int medium,
                                                        @Param("hard") int hard);
    
    /**
     * 获取搜索建议（基于题目标题）
     */
    @Select("SELECT DISTINCT title FROM questions " +
            "WHERE title LIKE CONCAT('%', #{keyword}, '%') " +
            "LIMIT 10")
    List<String> getSearchSuggestions(@Param("keyword") String keyword);
} 