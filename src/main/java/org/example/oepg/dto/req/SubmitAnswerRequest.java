package org.example.oepg.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {

    @NotNull
    private Long recordId; // 前端可携带，也可根据 examId+studentId 定位

    @NotNull
    private List<AnswerItem> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerItem {
        @NotNull
        private Long questionId;
        @NotNull
        private String answer; // 文本或序列化字符串（选择题为选项标识、填空题可用分隔符表示多空）
        private Integer timeUsedSeconds; // 可选：作答耗时
    }
}
