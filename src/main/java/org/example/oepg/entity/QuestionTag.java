package org.example.oepg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 题目标签实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("question_tags")
public class QuestionTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("question_id")
    private Long questionId; // 题目ID

    @TableField("tag_name")
    private String tagName; // 标签名称

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
