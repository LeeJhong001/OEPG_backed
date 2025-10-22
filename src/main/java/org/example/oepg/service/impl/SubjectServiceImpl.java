package org.example.oepg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.oepg.entity.Subject;
import org.example.oepg.mapper.SubjectMapper;
import org.example.oepg.service.SubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 科目服务实现类
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override
    public List<Subject> getEnabledSubjects() {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getEnabled, true)
               .orderByAsc(Subject::getName);
        return list(wrapper);
    }

    @Override
    public Subject getSubjectById(Long id) {
        return getById(id);
    }
}
