package org.example.oepg.controller;

import org.example.oepg.entity.Subject;
import org.example.oepg.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科目控制器
 */
@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 获取所有启用的科目列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<Map<String, Object>> getEnabledSubjects() {
        List<Subject> subjects = subjectService.getEnabledSubjects();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取科目列表成功");
        response.put("data", subjects);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取科目详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSubjectById(@PathVariable Long id) {
        Subject subject = subjectService.getSubjectById(id);
        
        Map<String, Object> response = new HashMap<>();
        if (subject != null) {
            response.put("code", 200);
            response.put("message", "获取科目详情成功");
            response.put("data", subject);
        } else {
            response.put("code", 404);
            response.put("message", "科目不存在");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有科目列表（包括禁用的）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSubjects() {
        List<Subject> subjects = subjectService.list();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取科目列表成功");
        response.put("data", subjects);
        
        return ResponseEntity.ok(response);
    }
}
