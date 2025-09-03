package org.example.oepg.dto.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 题目批量操作请求DTO
 */
@Data
public class QuestionBatchRequest {

    @NotEmpty(message = "题目ID列表不能为空")
    private List<Long> questionIds;

    private Long categoryId; // 批量更新分类
    private Integer difficulty; // 批量更新难度
    private Boolean enabled; // 批量启用/禁用
    private List<String> tags; // 批量添加标签
}
