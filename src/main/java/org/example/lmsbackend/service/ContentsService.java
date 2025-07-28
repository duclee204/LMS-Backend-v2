package org.example.lmsbackend.service;

import org.example.lmsbackend.dto.ContentsDTO;
import org.example.lmsbackend.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentsService {
    
    @Autowired
    private ContentService contentService;
    
    // Delegate methods to ContentService
    public List<Content> getContentsByModuleId(int moduleId) {
        return contentService.getContentsByModuleId(moduleId);
    }
    
    public List<Content> getContentsByCourseId(int courseId) {
        return contentService.getContentsByCourseId(courseId);
    }
    
    public Content updateContentStatus(int contentId, boolean published, String username) {
        return contentService.updateContentStatus(contentId, published, username);
    }
    
    // Placeholder methods for controller compatibility
    public void createContent(ContentsDTO dto) {
        // TODO: Implement content creation using ContentService
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public void updateContent(ContentsDTO dto) {
        // TODO: Implement content update using ContentService
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public void deleteContent(int contentId) {
        // TODO: Implement content deletion using ContentService
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public Integer getModuleIdByContentId(int contentId) {
        // TODO: Implement this method using ContentService
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
