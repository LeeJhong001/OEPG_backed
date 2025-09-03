package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.oepg.entity.PaperQuestion;

import java.util.List;

/**
 * 试卷题目关联数据访问层
 */
@Mapper
public interface PaperQuestionRepository extends BaseMapper<PaperQuestion> {

    /**
     * 根据试卷ID获取题目列表
     */
    @Select("SELECT * FROM paper_questions WHERE paper_id = #{paperId} ORDER BY sort_order ASC")
    List<PaperQuestion> findByPaperId(Long paperId);

    /**
     * 根据题目ID获取关联的试卷列表
     */
    @Select("SELECT * FROM paper_questions WHERE question_id = #{questionId}")
    List<PaperQuestion> findByQuestionId(Long questionId);

    /**
     * 删除试卷的所有题目
     */
    @Select("DELETE FROM paper_questions WHERE paper_id = #{paperId}")
    void deleteByPaperId(Long paperId);
}
