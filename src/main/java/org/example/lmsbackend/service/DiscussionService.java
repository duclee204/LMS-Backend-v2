package org.example.lmsbackend.service;

import org.example.lmsbackend.dto.DiscussionDTO;
import org.example.lmsbackend.model.Course;
import org.example.lmsbackend.model.Discussion;
import org.example.lmsbackend.model.DiscussionType;
import org.example.lmsbackend.model.User;
import org.example.lmsbackend.repository.CourseRepository;
import org.example.lmsbackend.repository.DiscussionRepository;
import org.example.lmsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscussionService {
    @Autowired
    private DiscussionRepository discussionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    public List<DiscussionDTO> getAllDiscussions() {
        return discussionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DiscussionDTO> getDiscussionsByCourse(Integer courseId) {
        return discussionRepository.findPublicDiscussionsByCourse(courseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DiscussionDTO> getDiscussionsForUser(Integer courseId, Integer userId) {
        return discussionRepository.findDiscussionsForUser(courseId, userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DiscussionDTO createDiscussion(DiscussionDTO dto) {
        System.out.println("??? Creating discussion - DTO title: '" + dto.getTitle() + "', type: " + dto.getType());
        Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (courseOpt.isEmpty() || userOpt.isEmpty()) return null;
        
        Discussion discussion = new Discussion();
        discussion.setCourse(courseOpt.get());
        discussion.setUser(userOpt.get());
        discussion.setTitle(dto.getTitle());
        discussion.setContent(dto.getContent());
        
        // Set attachment info
        discussion.setAttachmentUrl(dto.getAttachmentUrl());
        discussion.setAttachmentName(dto.getAttachmentName());
        
        // Set discussion type
        if ("PRIVATE".equals(dto.getType())) {
            discussion.setType(DiscussionType.PRIVATE);
            
            // Set target user for private discussions
            if (dto.getTargetUserId() != null) {
                Optional<User> targetUserOpt = userRepository.findById(dto.getTargetUserId());
                if (targetUserOpt.isPresent()) {
                    discussion.setTargetUser(targetUserOpt.get());
                    System.out.println("??? Private discussion to user: " + targetUserOpt.get().getFullName());
                }
            }
        } else {
            discussion.setType(DiscussionType.PUBLIC);
            System.out.println("??? Public discussion");
        }
        
        System.out.println("??? About to save discussion with title: '" + discussion.getTitle() + "', type: " + discussion.getType());
        Discussion saved = discussionRepository.save(discussion);
        return toDTO(saved);
    }

    public DiscussionDTO getDiscussionById(Integer discussionId) {
        Optional<Discussion> discussionOpt = discussionRepository.findById(discussionId);
        if (discussionOpt.isEmpty()) return null;
        return toDTO(discussionOpt.get());
    }

    public DiscussionDTO updateDiscussion(Integer id, DiscussionDTO dto) {
        Optional<Discussion> discussionOpt = discussionRepository.findById(id);
        if (discussionOpt.isEmpty()) return null;
        Discussion discussion = discussionOpt.get();
        discussion.setTitle(dto.getTitle());
        discussion.setContent(dto.getContent());
        
        // Update attachment info
        discussion.setAttachmentUrl(dto.getAttachmentUrl());
        discussion.setAttachmentName(dto.getAttachmentName());
        
        Discussion saved = discussionRepository.save(discussion);
        return toDTO(saved);
    }

    public boolean deleteDiscussion(Integer id) {
        if (!discussionRepository.existsById(id)) return false;
        discussionRepository.deleteById(id);
        return true;
    }

    public boolean deleteDiscussion(Integer id, Integer userId, boolean isAdmin) {
        Optional<Discussion> discussionOpt = discussionRepository.findById(id);
        if (discussionOpt.isEmpty()) return false;
        
        Discussion discussion = discussionOpt.get();
        
        // Admin can delete any discussion, others can only delete their own
        if (!isAdmin && !discussion.getUser().getUserId().equals(userId)) {
            return false;
        }
        
        discussionRepository.deleteById(id);
        return true;
    }

    public DiscussionDTO toDTO(Discussion discussion) {
        DiscussionDTO dto = new DiscussionDTO();
        dto.setId(discussion.getId());
        dto.setCourseId(discussion.getCourse().getCourseId());
        dto.setUserId(discussion.getUser().getUserId());
        dto.setUserName(discussion.getUser().getFullName() != null ? 
                       discussion.getUser().getFullName() : discussion.getUser().getUsername());
        dto.setUserRole(discussion.getUser().getRole().toString());
        dto.setTitle(discussion.getTitle());
        dto.setContent(discussion.getContent());
        dto.setType(discussion.getType().toString());
        
        // Set target user info for private discussions
        if (discussion.getTargetUser() != null) {
            dto.setTargetUserId(discussion.getTargetUser().getUserId());
            dto.setTargetUserName(discussion.getTargetUser().getFullName() != null ?
                                 discussion.getTargetUser().getFullName() : discussion.getTargetUser().getUsername());
        }
        
        // Set attachment info
        dto.setAttachmentUrl(discussion.getAttachmentUrl());
        dto.setAttachmentName(discussion.getAttachmentName());
        
        dto.setCreatedAt(discussion.getCreatedAt());
        
        return dto;
    }
}
