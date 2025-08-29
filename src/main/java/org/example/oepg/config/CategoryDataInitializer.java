package org.example.oepg.config;

import org.example.oepg.entity.QuestionCategory;
import org.example.oepg.repository.QuestionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 分类数据初始化器
 * 在应用启动时自动创建一些基本的测试分类
 */
@Component
public class CategoryDataInitializer implements CommandLineRunner {

    @Autowired
    private QuestionCategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有分类，如果没有则创建默认分类
        if (categoryRepository.selectCount(null) == 0) {
            createDefaultCategories();
        }
    }

    private void createDefaultCategories() {
        try {
            // 创建数学分类
            QuestionCategory mathCategory = QuestionCategory.builder()
                    .name("数学")
                    .parentId(null)
                    .description("数学相关题目")
                    .sortOrder(1)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            categoryRepository.insert(mathCategory);
            System.out.println("已创建数学分类");

            // 创建高等数学子分类
            QuestionCategory advancedMathCategory = QuestionCategory.builder()
                    .name("高等数学")
                    .parentId(mathCategory.getId())
                    .description("高等数学题目")
                    .sortOrder(1)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            categoryRepository.insert(advancedMathCategory);
            System.out.println("已创建高等数学分类");

            // 创建微积分子分类
            QuestionCategory calculusCategory = QuestionCategory.builder()
                    .name("微积分")
                    .parentId(advancedMathCategory.getId())
                    .description("微积分题目")
                    .sortOrder(1)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            categoryRepository.insert(calculusCategory);
            System.out.println("已创建微积分分类");

            // 创建物理分类
            QuestionCategory physicsCategory = QuestionCategory.builder()
                    .name("物理")
                    .parentId(null)
                    .description("物理相关题目")
                    .sortOrder(2)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            categoryRepository.insert(physicsCategory);
            System.out.println("已创建物理分类");

            // 创建力学子分类
            QuestionCategory mechanicsCategory = QuestionCategory.builder()
                    .name("力学")
                    .parentId(physicsCategory.getId())
                    .description("力学题目")
                    .sortOrder(1)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            categoryRepository.insert(mechanicsCategory);
            System.out.println("已创建力学分类");

            System.out.println("分类数据初始化完成！");
            
        } catch (Exception e) {
            System.err.println("分类数据初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 