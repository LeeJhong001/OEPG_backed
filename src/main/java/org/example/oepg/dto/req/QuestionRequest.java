package org.example.oepg.dto.req;

import lombok.Data;
import org.example.oepg.entity.Question;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 题目请求 DTO
 */
@Data
public class QuestionRequest {
    
    @NotBlank(message = "题目标题不能为空")
    @Size(max = 500, message = "题目标题长度不能超过500个字符")
    private String title;
    
    @NotBlank(message = "题目内容不能为空")
    private String content;
    
    @NotNull(message = "题目类型不能为空")
    private Question.QuestionType type;
    
    @NotNull(message = "题目难度不能为空")
    private Integer difficulty;
    
    private Long categoryId; // 题目所属分类ID
    
    @Size(max = 1000, message = "题目答案长度不能超过1000个字符")
    private String answer;
    
    @Size(max = 1000, message = "题目解析长度不能超过1000个字符")
    private String analysis;
    
    @Size(max = 2000, message = "题目选项长度不能超过2000个字符")
    private String options; // JSON格式的选项
    
    @NotNull(message = "题目分值不能为空")
    private Integer score;
} 