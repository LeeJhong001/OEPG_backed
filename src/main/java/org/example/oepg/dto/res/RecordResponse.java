package org.example.oepg.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse {
    private Long recordId;
    private Long examId;
    private Long paperId;
    private Integer score;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
}
