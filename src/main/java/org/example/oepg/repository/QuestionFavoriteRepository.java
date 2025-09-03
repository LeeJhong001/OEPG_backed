package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.example.oepg.entity.QuestionFavorite;

import java.util.List;

/**
 * 题目收藏 Repository
 */
@Mapper
public interface QuestionFavoriteRepository extends BaseMapper<QuestionFavorite> {

    /**
     * 根据用户ID获取收藏的题目ID列表
     */
    @Select("SELECT question_id FROM question_favorites WHERE user_id = #{userId}")
    List<Long> findQuestionIdsByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否收藏了某个题目
     */
    @Select("SELECT COUNT(*) > 0 FROM question_favorites WHERE user_id = #{userId} AND question_id = #{questionId}")
    boolean existsByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /**
     * 删除用户对某个题目的收藏
     */
    @Delete("DELETE FROM question_favorites WHERE user_id = #{userId} AND question_id = #{questionId}")
    int deleteByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);
}
