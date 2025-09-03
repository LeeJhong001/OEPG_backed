package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.oepg.entity.ExamPaper;

import java.util.List;

/**
 * 试卷数据访问层
 */
@Mapper
public interface ExamPaperRepository extends BaseMapper<ExamPaper> {

    /**
     * 根据考试ID获取试卷列表
     */
    @Select("SELECT * FROM exam_papers WHERE exam_id = #{examId} ORDER BY created_at DESC")
    List<ExamPaper> findByExamId(Long examId);

    /**
     * 根据创建者ID获取试卷列表
     */
    @Select("SELECT * FROM exam_papers WHERE created_by = #{createdById} ORDER BY created_at DESC")
    List<ExamPaper> findByCreatedById(Long createdById);

    /**
     * 根据状态获取试卷列表
     */
    @Select("SELECT * FROM exam_papers WHERE status = #{status} ORDER BY created_at DESC")
    List<ExamPaper> findByStatus(String status);
}
