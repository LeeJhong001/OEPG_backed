package org.example.oepg.service;

import org.example.oepg.dto.req.SubmitAnswerRequest;
import org.example.oepg.dto.res.RecordResponse;
import org.example.oepg.dto.res.StartExamResponse;
import org.example.oepg.dto.res.SubmitAnswerResponse;

import java.util.List;

public interface StudentExamService {

    List<StartExamResponse.AvailableExam> getAvailableExams(Long studentId);

    StartExamResponse startExam(Long examId, Long studentId);

    SubmitAnswerResponse submitExam(Long examId, Long studentId, SubmitAnswerRequest request);

    List<RecordResponse> getMyRecords(Long studentId);

    RecordResponse getRecordDetail(Long recordId, Long studentId);
}
