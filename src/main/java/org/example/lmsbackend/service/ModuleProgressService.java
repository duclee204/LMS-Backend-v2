package org.example.lmsbackend.service;

import org.example.lmsbackend.model.*;
import org.example.lmsbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ModuleProgressService {

    @Autowired
    private ModuleProgressMapper moduleProgressMapper;
    
    @Autowired
    private ContentProgressRepository contentProgressRepository;
    
    @Autowired
    private VideoProgressRepository videoProgressRepository;
    
    @Autowired
    private ContentRepository contentRepository;
    
    @Autowired
    private VideoRepository videoRepository;
    
    @Autowired
    private UserQuizAttemptMapper userQuizAttemptMapper;

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
        
        // Update overall module progress
        updateModuleProgressFromContent(userId, moduleId);
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

    // Individual Content Progress Methods
    public void markContentAsViewed(Integer userId, Integer contentId) {
        Optional<ContentProgress> existingProgress = contentProgressRepository.findByUserIdAndContentId(userId, contentId);
        
        ContentProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setStatus("completed");
            progress.setCompletedAt(java.time.Instant.now());
            contentProgressRepository.save(progress);
        } else {
            // Create new progress
            progress = new ContentProgress();
            
            User user = new User();
            user.setUserId(userId);
            progress.setUser(user);
            
            Content content = contentRepository.findById(contentId).orElse(null);
            if (content != null) {
                progress.setContent(content);
                progress.setStatus("completed");
                progress.setAccessedAt(java.time.Instant.now());
                progress.setCompletedAt(java.time.Instant.now());
                contentProgressRepository.save(progress);
                
                // Update module progress after content completion
                updateModuleProgressFromContent(userId, content.getModule().getId());
            }
        }
        
        // If content exists and we have progress, update module progress
        if (progress != null && progress.getContent() != null) {
            updateModuleProgressFromContent(userId, progress.getContent().getModule().getId());
        }
    }
    
    public Map<String, Object> getContentProgress(Integer userId, Integer contentId) {
        Optional<ContentProgress> progress = contentProgressRepository.findByUserIdAndContentId(userId, contentId);
        Map<String, Object> result = new HashMap<>();
        
        if (progress.isPresent()) {
            ContentProgress cp = progress.get();
            boolean isCompleted = "completed".equals(cp.getStatus());
            result.put("viewed", isCompleted);
            result.put("completed", isCompleted); // Add for frontend compatibility
            result.put("viewedAt", cp.getCompletedAt());
            result.put("status", cp.getStatus());
        } else {
            result.put("viewed", false);
            result.put("completed", false); // Add for frontend compatibility
            result.put("viewedAt", null);
            result.put("status", "not_accessed");
        }
        
        return result;
    }
    
    // Individual Video Progress Methods
    public void updateVideoWatchProgress(Integer userId, Integer videoId, Double watchedDuration, 
                                       Double totalDuration, Double watchedPercentage, Boolean completed) {
        Optional<VideoProgress> existingProgress = videoProgressRepository.findByUserIdAndVideoId(userId, videoId);
        
        VideoProgress progress;
        Video video = null;
        
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setWatchedDuration(watchedDuration.intValue());
            progress.setTotalDuration(totalDuration.intValue());
            progress.setLastWatchedAt(LocalDateTime.now());
            videoProgressRepository.save(progress);
            video = progress.getVideo();
        } else {
            // Create new progress
            progress = new VideoProgress();
            
            User user = new User();
            user.setUserId(userId);
            progress.setUser(user);
            
            video = videoRepository.findById(videoId.longValue()).orElse(null);
            if (video != null) {
                progress.setVideo(video);
                progress.setWatchedDuration(watchedDuration.intValue());
                progress.setTotalDuration(totalDuration.intValue());
                progress.setLastWatchedAt(LocalDateTime.now());
                videoProgressRepository.save(progress);
            }
        }
        
        // Update module progress if video belongs to a module
        if (video != null && video.getModule() != null) {
            updateModuleProgressFromContent(userId, video.getModule().getId());
        }
    }
    
    public Map<String, Object> getVideoProgress(Integer userId, Integer videoId) {
        Optional<VideoProgress> progress = videoProgressRepository.findByUserIdAndVideoId(userId, videoId);
        Map<String, Object> result = new HashMap<>();
        
        if (progress.isPresent()) {
            VideoProgress vp = progress.get();
            double percentage = vp.getTotalDuration() > 0 ? 
                (double) vp.getWatchedDuration() / vp.getTotalDuration() * 100 : 0;
            
            result.put("watchedDuration", vp.getWatchedDuration());
            result.put("totalDuration", vp.getTotalDuration());
            result.put("watchedPercentage", percentage);
            result.put("completed", percentage >= 90); // 90% threshold
            result.put("lastWatchedAt", vp.getLastWatchedAt());
        } else {
            result.put("watchedDuration", 0);
            result.put("totalDuration", 0);
            result.put("watchedPercentage", 0.0);
            result.put("completed", false);
            result.put("lastWatchedAt", null);
        }
        
        return result;
    }
    
    // Get module progress with detailed breakdown
    public ModuleProgress getModuleProgress(Integer userId, Integer moduleId) {
        Optional<ModuleProgress> progress = moduleProgressMapper.findByUserAndModule(userId, moduleId);
        
        if (progress.isPresent()) {
            return progress.get();
        } else {
            // Create new empty progress
            return getOrCreateProgress(userId, moduleId);
        }
    }
    
    // Automatically update module progress based on individual content/video/test completion
    public void updateModuleProgressFromContent(Integer userId, Integer moduleId) {
        try {
            System.out.println("🔄 Updating module progress for userId: " + userId + ", moduleId: " + moduleId);
            
            ModuleProgress progress = getOrCreateProgress(userId, moduleId);
            
            // Check actual content completion status
            boolean contentCompleted = hasCompletedContent(userId, moduleId);
            boolean videoCompleted = hasCompletedVideos(userId, moduleId);
            boolean testCompleted = hasPassedQuizzes(userId, moduleId);
            
            // Update progress flags
            progress.setContentCompleted(contentCompleted);
            progress.setVideoCompleted(videoCompleted);
            progress.setTestCompleted(testCompleted);
            
            // Determine overall module completion
            boolean allCompleted = contentCompleted && videoCompleted && testCompleted;
            progress.setModuleCompleted(allCompleted);
            
            if (allCompleted && progress.getCompletedAt() == null) {
                progress.setCompletedAt(LocalDateTime.now());
            }
            
            // Save updated progress
            moduleProgressMapper.update(progress);
            
            System.out.println("✅ Module progress updated: " + contentCompleted + "/" + videoCompleted + "/" + testCompleted);
            
        } catch (Exception e) {
            System.err.println("❌ Error updating module progress: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Check if user has completed at least one content in the module
    public boolean hasCompletedContent(Integer userId, Integer moduleId) {
        // Get all content progress for this user and module
        List<ContentProgress> contentProgressList = contentProgressRepository.findByUserIdAndModuleId(userId, moduleId);
        return contentProgressList.stream()
            .anyMatch(cp -> "completed".equals(cp.getStatus()));
    }
    
    // Check if user has completed a specific content item by content ID
    public boolean hasCompletedSpecificContent(Integer userId, Integer contentId) {
        try {
            Optional<ContentProgress> contentProgress = contentProgressRepository.findByUserIdAndContentId(userId, contentId);
            return contentProgress.isPresent() && "completed".equals(contentProgress.get().getStatus());
        } catch (Exception e) {
            System.err.println("❌ Error checking specific content completion: " + e.getMessage());
            return false;
        }
    }
    
    // Check if user has completed at least one video in the module  
    public boolean hasCompletedVideos(Integer userId, Integer moduleId) {
        // Get all video progress for this user and module
        List<VideoProgress> videoProgressList = videoProgressRepository.findByUserIdAndModuleId(userId, moduleId);
        return videoProgressList.stream()
            .anyMatch(vp -> {
                double percentage = vp.getTotalDuration() > 0 ? 
                    (double) vp.getWatchedDuration() / vp.getTotalDuration() * 100 : 0;
                return percentage >= 90; // 90% threshold for video completion
            });
    }
    
    // Check if user has completed a specific video item
    public boolean hasCompletedSpecificVideo(Integer userId, Integer videoId) {
        try {
            Optional<VideoProgress> videoProgress = videoProgressRepository.findByUserIdAndVideoId(userId, videoId);
            if (videoProgress.isPresent()) {
                VideoProgress vp = videoProgress.get();
                double percentage = vp.getTotalDuration() > 0 ? 
                    (double) vp.getWatchedDuration() / vp.getTotalDuration() * 100 : 0;
                return percentage >= 90; // 90% threshold for video completion
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error checking specific video completion: " + e.getMessage());
            return false;
        }
    }
    
    // Check if user has completed a specific quiz
    public boolean hasCompletedSpecificQuiz(Integer userId, Integer quizId) {
        try {
            UserQuizAttempt attempt = userQuizAttemptMapper.findByUserAndQuiz(userId, quizId);
            return attempt != null; // If user has attempted the quiz, consider it completed
        } catch (Exception e) {
            System.err.println("❌ Error checking specific quiz completion: " + e.getMessage());
            return false;
        }
    }
    
    // Check if user has passed at least one quiz in the module
    private boolean hasPassedQuizzes(Integer userId, Integer moduleId) {
        try {
            // For now, use testCompleted flag from module progress as fallback
            // TODO: Implement proper quiz completion checking
            ModuleProgress progress = getOrCreateProgress(userId, moduleId);
            return progress.getTestCompleted() != null && progress.getTestCompleted();
        } catch (Exception e) {
            System.err.println("❌ Error checking quiz completion: " + e.getMessage());
            return false;
        }
    }
}
