package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.example.oepg.entity.QuestionTag;

import java.util.List;

/**
 * 题目标签 Repository
 */
@Mapper
public interface QuestionTagRepository extends BaseMapper<QuestionTag> {

    /**
     * 根据题目ID获取标签列表
     */
    @Select("SELECT tag_name FROM question_tags WHERE question_id = #{questionId}")
    List<String> findTagNamesByQuestionId(@Param("questionId") Long questionId);

    /**
     * 根据标签名称获取题目ID列表
     */
    @Select("SELECT question_id FROM question_tags WHERE tag_name = #{tagName}")
    List<Long> findQuestionIdsByTagName(@Param("tagName") String tagName);

    /**
     * 删除题目的所有标签
     */
    @Delete("DELETE FROM question_tags WHERE question_id = #{questionId}")
    int deleteByQuestionId(@Param("questionId") Long questionId);

    /**
     * 获取所有标签名称
     */
    @Select("SELECT DISTINCT tag_name FROM question_tags ORDER BY tag_name")
    List<String> findAllTagNames();
}
