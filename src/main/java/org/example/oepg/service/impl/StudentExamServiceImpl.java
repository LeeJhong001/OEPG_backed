package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.oepg.dto.req.SubmitAnswerRequest;
import org.example.oepg.dto.res.StartExamResponse;
import org.example.oepg.dto.res.SubmitAnswerResponse;
import org.example.oepg.dto.res.RecordResponse;
import org.example.oepg.entity.*;
import org.example.oepg.repository.*;
import org.example.oepg.service.StudentExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentExamServiceImpl implements StudentExamService {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamPaperRepository examPaperRepository;
    @Autowired
    private PaperQuestionRepository paperQuestionRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ExamRecordRepository examRecordRepository;

    @Override
    public List<StartExamResponse.AvailableExam> getAvailableExams(Long studentId) {
        // 可参加的考试：已发布，且当前时间在开始和结束之间，且未提交/未完成
        List<Exam> ongoing = examRepository.findOngoingExams();
        LocalDateTime now = LocalDateTime.now();
        // 基于时间窗口的发布考试（可能尚未到开始时间，这里一并展示）
        List<Exam> upcoming = examRepository.findUpcomingExams();
        List<Exam> all = new ArrayList<>();
        all.addAll(ongoing);
        all.addAll(upcoming);
        // 去重
        Map<Long, Exam> map = new LinkedHashMap<>();
        for (Exam e : all) map.put(e.getId(), e);

        // 过滤掉已经提交/评分完成的考试
        return map.values().stream()
                .filter(exam -> hasPublishedPaper(exam.getId()))
                .filter(exam -> !hasFinishedRecord(studentId, exam.getId()))
                .map(this::toAvailableExam)
                .sorted(Comparator.comparing(StartExamResponse.AvailableExam::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public StartExamResponse startExam(Long examId, Long studentId) {
        Exam exam = examRepository.selectById(examId);
        if (exam == null) throw new RuntimeException("考试不存在");
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            throw new RuntimeException("考试尚未开始");
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            throw new RuntimeException("考试已结束");
        }
        if (exam.getStatus() == Exam.ExamStatus.ARCHIVED || exam.getStatus() == Exam.ExamStatus.DRAFT) {
            throw new RuntimeException("考试不可参加");
        }

        // 获取一份发布状态的试卷
        ExamPaper paper = examPaperRepository.findByExamId(examId).stream()
                .filter(p -> p.getStatus() == ExamPaper.PaperStatus.PUBLISHED)
                .findFirst().orElse(null);
        if (paper == null) throw new RuntimeException("考试无可用试卷");

        // 是否存在未完成的记录
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).eq("exam_id", examId).eq("status", ExamRecord.RecordStatus.ONGOING);
        ExamRecord ongoing = examRecordRepository.selectOne(wrapper);
        if (ongoing != null) {
            return buildStartResponse(ongoing, exam, paper);
        }

        // 创建新的记录
        ExamRecord record = ExamRecord.builder()
                .examId(examId)
                .paperId(paper.getId())
                .studentId(studentId)
                .totalScore(paper.getTotalScore())
                .startTime(now)
                .status(ExamRecord.RecordStatus.ONGOING)
                .build();
        examRecordRepository.insert(record);

        return buildStartResponse(record, exam, paper);
    }

    @Override
    public SubmitAnswerResponse submitExam(Long examId, Long studentId, SubmitAnswerRequest request) {
        ExamRecord record = examRecordRepository.selectById(request.getRecordId());
        if (record == null || !Objects.equals(record.getStudentId(), studentId) || !Objects.equals(record.getExamId(), examId)) {
            throw new RuntimeException("记录不存在或无权限");
        }
        if (record.getStatus() == ExamRecord.RecordStatus.SUBMITTED || record.getStatus() == ExamRecord.RecordStatus.GRADED) {
            throw new RuntimeException("该记录已提交");
        }

        Exam exam = examRepository.selectById(examId);
        LocalDateTime now = LocalDateTime.now();
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            record.setStatus(ExamRecord.RecordStatus.TIMEOUT);
            examRecordRepository.updateById(record);
            throw new RuntimeException("已超过考试结束时间");
        }
        // 时长校验
        if (exam.getDuration() != null && record.getStartTime() != null) {
            long minutes = Duration.between(record.getStartTime(), now).toMinutes();
            if (minutes > exam.getDuration()) {
                record.setStatus(ExamRecord.RecordStatus.TIMEOUT);
                examRecordRepository.updateById(record);
                throw new RuntimeException("已超过考试时长");
            }
        }

        // 构建试卷题目映射
        List<PaperQuestion> pqs = paperQuestionRepository.findByPaperId(record.getPaperId());
        Map<Long, PaperQuestion> pqByQuestionId = pqs.stream().collect(Collectors.toMap(PaperQuestion::getQuestionId, x -> x));
        List<Long> questionIds = pqs.stream().map(PaperQuestion::getQuestionId).collect(Collectors.toList());
        Map<Long, Question> questionMap = questionIds.isEmpty() ? Collections.emptyMap() :
                questionRepository.selectBatchIds(questionIds).stream().collect(Collectors.toMap(Question::getId, q -> q));

        // 自动批改客观题
        int objectiveScore = 0;
        boolean hasSubjective = false;
        for (SubmitAnswerRequest.AnswerItem ans : request.getAnswers()) {
            Question q = questionMap.get(ans.getQuestionId());
            if (q == null) continue;
            if (q.getType() == Question.QuestionType.CHOICE) {
                if (normalize(ans.getAnswer()).equalsIgnoreCase(normalize(q.getAnswer()))) {
                    objectiveScore += safeScore(pqByQuestionId.get(q.getId()));
                }
            } else if (q.getType() == Question.QuestionType.FILL_BLANK) {
                if (fillBlankMatch(ans.getAnswer(), q.getAnswer())) {
                    objectiveScore += safeScore(pqByQuestionId.get(q.getId()));
                }
            } else {
                hasSubjective = true;
            }
        }

        int totalScore = objectiveScore; // 先不计主观题
        record.setScore(totalScore);
        record.setSubmitTime(now);
        record.setStatus(hasSubjective ? ExamRecord.RecordStatus.SUBMITTED : ExamRecord.RecordStatus.GRADED);
        // 保存原始答案 JSON
        String answersJson = toAnswersJson(request);
        record.setAnswers(answersJson);
        examRecordRepository.updateById(record);

        return SubmitAnswerResponse.builder()
                .recordId(record.getId())
                .objectiveScore(objectiveScore)
                .totalScore(totalScore)
                .pendingReview(hasSubjective)
                .build();
    }

    @Override
    public List<RecordResponse> getMyRecords(Long studentId) {
        QueryWrapper<ExamRecord> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId).orderByDesc("created_at");
        List<ExamRecord> list = examRecordRepository.selectList(qw);
        return list.stream().map(this::toRecordResponse).collect(Collectors.toList());
    }

    @Override
    public RecordResponse getRecordDetail(Long recordId, Long studentId) {
        ExamRecord r = examRecordRepository.selectById(recordId);
        if (r == null || !Objects.equals(r.getStudentId(), studentId)) {
            throw new RuntimeException("记录不存在或无权限");
        }
        return toRecordResponse(r);
    }

    private boolean hasPublishedPaper(Long examId) {
        return examPaperRepository.findByExamId(examId).stream()
                .anyMatch(p -> p.getStatus() == ExamPaper.PaperStatus.PUBLISHED);
    }

    private boolean hasFinishedRecord(Long studentId, Long examId) {
        QueryWrapper<ExamRecord> qw = new QueryWrapper<>();
        qw.eq("student_id", studentId).eq("exam_id", examId)
                .in("status", ExamRecord.RecordStatus.SUBMITTED, ExamRecord.RecordStatus.GRADED);
        return examRecordRepository.selectCount(qw) > 0;
    }

    private StartExamResponse.AvailableExam toAvailableExam(Exam exam) {
        return StartExamResponse.AvailableExam.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .durationMinutes(exam.getDuration())
                .totalScore(exam.getTotalScore())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .build();
    }

    private StartExamResponse buildStartResponse(ExamRecord record, Exam exam, ExamPaper paper) {
        List<PaperQuestion> pqs = paperQuestionRepository.findByPaperId(paper.getId());
        List<Long> qids = pqs.stream().map(PaperQuestion::getQuestionId).collect(Collectors.toList());
        Map<Long, Question> qmap = qids.isEmpty() ? Collections.emptyMap() :
                questionRepository.selectBatchIds(qids).stream().collect(Collectors.toMap(Question::getId, q -> q));

        List<StartExamResponse.ExamQuestion> questions = pqs.stream()
                .sorted(Comparator.comparing(PaperQuestion::getSortOrder))
                .map(pq -> {
                    Question q = qmap.get(pq.getQuestionId());
                    return StartExamResponse.ExamQuestion.builder()
                            .questionId(q.getId())
                            .title(q.getTitle())
                            .content(q.getContent())
                            .type(q.getType().name())
                            .difficulty(q.getDifficulty())
                            .score(pq.getScore())
                            .sortOrder(pq.getSortOrder())
                            .options(q.getOptions())
                            .build();
                }).collect(Collectors.toList());

        return StartExamResponse.builder()
                .recordId(record.getId())
                .examId(exam.getId())
                .paperId(paper.getId())
                .examTitle(exam.getTitle())
                .durationMinutes(exam.getDuration())
                .totalScore(paper.getTotalScore())
                .startTime(record.getStartTime())
                .endTime(exam.getEndTime())
                .questions(questions)
                .build();
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }

    private boolean fillBlankMatch(String user, String standard) {
        if (user == null || standard == null) return false;
        // 支持多空：用 | 或 ; 作为分隔符进行对比（忽略空白和大小写）
        String[] ua = user.split("[|;]");
        String[] sa = standard.split("[|;]");
        if (ua.length != sa.length) return false;
        for (int i = 0; i < ua.length; i++) {
            if (!normalize(ua[i]).equalsIgnoreCase(normalize(sa[i]))) return false;
        }
        return true;
    }

    private int safeScore(PaperQuestion pq) {
        return pq != null && pq.getScore() != null ? pq.getScore() : 0;
    }

    private String toAnswersJson(SubmitAnswerRequest request) {
        // 简单拼接为 JSON 数组字符串，避免引入额外依赖
        String items = request.getAnswers().stream().map(a ->
                String.format(Locale.ROOT, "{\"questionId\":%d,\"answer\":%s%s}",
                        a.getQuestionId(),
                        toJsonString(a.getAnswer()),
                        a.getTimeUsedSeconds() != null ? ",\"timeUsedSeconds\":" + a.getTimeUsedSeconds() : "")
        ).collect(Collectors.joining(","));
        return "[" + items + "]";
    }

    private String toJsonString(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }

    private RecordResponse toRecordResponse(ExamRecord r) {
        // 轻量实现，后续可补充更多字段（考试/试卷标题等）
        RecordResponse resp = new RecordResponse();
        resp.setRecordId(r.getId());
        resp.setExamId(r.getExamId());
        resp.setPaperId(r.getPaperId());
        resp.setScore(r.getScore());
        resp.setStatus(r.getStatus().name());
        resp.setStartTime(r.getStartTime());
        resp.setSubmitTime(r.getSubmitTime());
        return resp;
    }
}
