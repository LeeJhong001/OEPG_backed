package org.example.oepg.controller;

import org.example.oepg.entity.Subject;
import org.example.oepg.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员科目管理控制器
 */
@RestController
@RequestMapping("/api/admin/subjects")
@CrossOrigin(origins = "*")
public class AdminSubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 创建科目
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSubject(@RequestBody Subject subject) {
        Subject created = subjectService.save(subject);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "科目创建成功");
        response.put("data", created);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 更新科目
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSubject(
            @PathVariable Long id,
            @RequestBody Subject subject) {
        
        Subject existing = subjectService.getById(id);
        if (existing == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "科目不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        // 更新字段
        if (subject.getName() != null) {
            existing.setName(subject.getName());
        }
        if (subject.getCode() != null) {
            existing.setCode(subject.getCode());
        }
        if (subject.getDescription() != null) {
            existing.setDescription(subject.getDescription());
        }
        if (subject.getDepartment() != null) {
            existing.setDepartment(subject.getDepartment());
        }
        if (subject.getCredits() != null) {
            existing.setCredits(subject.getCredits());
        }
        if (subject.getEnabled() != null) {
            existing.setEnabled(subject.getEnabled());
        }
        
        Subject updated = subjectService.updateById(existing);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "科目更新成功");
        response.put("data", updated);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除科目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSubject(@PathVariable Long id) {
        Subject existing = subjectService.getById(id);
        if (existing == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "科目不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        subjectService.removeById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "科目删除成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 切换科目状态（启用/禁用）
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleSubjectStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        
        Subject existing = subjectService.getById(id);
        if (existing == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "科目不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        Boolean enabled = request.get("enabled");
        if (enabled != null) {
            existing.setEnabled(enabled);
            subjectService.updateById(existing);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "状态更新成功");
        response.put("data", existing);
        
        return ResponseEntity.ok(response);
    }
}
