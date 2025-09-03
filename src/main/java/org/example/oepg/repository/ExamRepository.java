package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.oepg.entity.Exam;

import java.util.List;

/**
 * 考试数据访问层
 */
@Mapper
public interface ExamRepository extends BaseMapper<Exam> {

    /**
     * 根据创建者ID获取考试列表
     */
    @Select("SELECT * FROM exams WHERE created_by = #{createdById} ORDER BY created_at DESC")
    List<Exam> findByCreatedById(Long createdById);

    /**
     * 根据科目ID获取考试列表
     */
    @Select("SELECT * FROM exams WHERE subject_id = #{subjectId} ORDER BY created_at DESC")
    List<Exam> findBySubjectId(Long subjectId);

    /**
     * 根据状态获取考试列表
     */
    @Select("SELECT * FROM exams WHERE status = #{status} ORDER BY created_at DESC")
    List<Exam> findByStatus(String status);

    /**
     * 获取正在进行的考试
     */
    @Select("SELECT * FROM exams WHERE status = 'ONGOING' AND start_time <= NOW() AND end_time >= NOW()")
    List<Exam> findOngoingExams();

    /**
     * 获取即将开始的考试
     */
    @Select("SELECT * FROM exams WHERE status = 'PUBLISHED' AND start_time > NOW() ORDER BY start_time ASC")
    List<Exam> findUpcomingExams();
}
