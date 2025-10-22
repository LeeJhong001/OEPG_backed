package org.example.oepg.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.oepg.entity.ExamRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生考试记录数据访问层
 */
@Mapper
public interface ExamRecordRepository extends BaseMapper<ExamRecord> {

    @Select("SELECT * FROM exam_records WHERE student_id = #{studentId} AND exam_id = #{examId} ORDER BY created_at DESC LIMIT 1")
    ExamRecord findLatestByStudentAndExam(@Param("studentId") Long studentId, @Param("examId") Long examId);

    @Select("SELECT * FROM exam_records WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<ExamRecord> findByStudent(@Param("studentId") Long studentId);

    @Update("UPDATE exam_records SET answers = #{answersJson}, submit_time = #{submitTime}, score = #{score}, status = #{status} WHERE id = #{recordId}")
    int updateSubmitResult(@Param("recordId") Long recordId,
                           @Param("answersJson") String answersJson,
                           @Param("submitTime") LocalDateTime submitTime,
                           @Param("score") Integer score,
                           @Param("status") String status);
}
