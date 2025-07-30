package org.example.lmsbackend.service;

import org.example.lmsbackend.model.ModuleProgress;
import org.example.lmsbackend.model.Modules;
import org.example.lmsbackend.model.User;
import org.example.lmsbackend.repository.ModuleProgressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleProgressService {

    @Autowired
    private ModuleProgressMapper moduleProgressMapper;

    public ModuleProgress getOrCreateProgress(Integer userId, Integer moduleId) {
        Optional<ModuleProgress> existingProgress = moduleProgressMapper.findByUserAndModule(userId, moduleId);
        
        if (existingProgress.isPresent()) {
            return existingProgress.get();
        }
        
        // Tạo progress mới
        ModuleProgress newProgress = new ModuleProgress();
        User user = new User();
        user.setUserId(userId);
        newProgress.setUser(user);
        
        Modules module = new Modules();
        module.setId(moduleId);
        newProgress.setModule(module);
        
        moduleProgressMapper.insert(newProgress);
        return newProgress;
    }

    public void updateContentProgress(Integer userId, Integer moduleId, boolean completed) {
        ModuleProgress progress = getOrCreateProgress(userId, moduleId);
        progress.setContentCompleted(completed);
        moduleProgressMapper.update(progress);
    }

    public void updateVideoProgress(Integer userId, Integer moduleId, boolean completed) {
        ModuleProgress progress = getOrCreateProgress(userId, moduleId);
        progress.setVideoCompleted(completed);
        moduleProgressMapper.update(progress);
    }

    public void updateTestProgress(Integer userId, Integer moduleId, boolean completed) {
        ModuleProgress progress = getOrCreateProgress(userId, moduleId);
        progress.setTestCompleted(completed);
        moduleProgressMapper.update(progress);
    }

    public boolean isTestUnlocked(Integer userId, Integer moduleId) {
        // Removed learning sequence restriction - students can access tests freely
        return true;
    }

    public boolean isModuleCompleted(Integer userId, Integer moduleId) {
        Optional<ModuleProgress> progress = moduleProgressMapper.findByUserAndModule(userId, moduleId);
        return progress.map(ModuleProgress::getModuleCompleted).orElse(false);
    }

    public boolean canAccessNextModule(Integer userId, Integer courseId, Integer currentModuleOrder) {
        // Kiểm tra xem module trước đó đã hoàn thành chưa
        if (currentModuleOrder <= 1) {
            return true; // Module đầu tiên luôn được phép truy cập
        }
        
        List<ModuleProgress> progressList = moduleProgressMapper.findByCourseAndUser(userId, courseId);
        
        // Kiểm tra module trước đó (order = currentModuleOrder - 1) đã hoàn thành chưa
        for (ModuleProgress progress : progressList) {
            // Cần thêm logic để kiểm tra order của module
            // Tạm thời return true, sẽ hoàn thiện sau
        }
        
        return true;
    }

    public List<ModuleProgress> getUserProgressInCourse(Integer userId, Integer courseId) {
        return moduleProgressMapper.findByCourseAndUser(userId, courseId);
    }
}
