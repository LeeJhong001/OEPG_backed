package org.example.oepg.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitAnswerResponse {
    private Long recordId;
    private Integer objectiveScore; // 客观题得分
    private Integer totalScore;     // 当前总分（若包含主观题，可能小于试卷总分）
    private boolean pendingReview;  // 是否包含主观题，待人工评分
}
