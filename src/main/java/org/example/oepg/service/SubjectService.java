package org.example.oepg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.oepg.entity.Subject;

import java.util.List;

/**
 * 科目服务接口
 */
public interface SubjectService extends IService<Subject> {
    
    /**
     * 获取所有启用的科目列表
     */
    List<Subject> getEnabledSubjects();
    
    /**
     * 根据ID获取科目
     */
    Subject getSubjectById(Long id);
}
