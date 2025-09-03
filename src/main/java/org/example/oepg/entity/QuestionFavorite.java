package org.example.oepg.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 题目收藏实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("question_favorites")
public class QuestionFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId; // 用户ID

    @TableField("question_id")
    private Long questionId; // 题目ID

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
