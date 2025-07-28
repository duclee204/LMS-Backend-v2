package org.example.lmsbackend.service;

import org.example.lmsbackend.dto.DiscussionReplyDTO;
import org.example.lmsbackend.model.Discussion;
import org.example.lmsbackend.model.DiscussionReply;
import org.example.lmsbackend.model.User;
import org.example.lmsbackend.repository.DiscussionReplyRepository;
import org.example.lmsbackend.repository.DiscussionRepository;
import org.example.lmsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DiscussionReplyService {

    @Autowired
    private DiscussionReplyRepository discussionReplyRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private UserRepository userRepository;

    public DiscussionReplyDTO createReply(DiscussionReplyDTO dto) {
        System.out.println("??? Creating reply - DiscussionId: " + dto.getDiscussionId() + ", UserId: " + dto.getUserId());
        
        Optional<Discussion> discussionOpt = discussionRepository.findById(dto.getDiscussionId());
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        
        if (discussionOpt.isEmpty() || userOpt.isEmpty()) {
            System.out.println("??? Discussion or User not found");
            return null;
        }

        DiscussionReply reply = new DiscussionReply();
        reply.setDiscussion(discussionOpt.get());
        reply.setUser(userOpt.get());
        reply.setContent(dto.getContent());
        
        // Set parent reply ID for nested replies
        reply.setParentReplyId(dto.getParentReplyId());
        
        // Set attachment info
        reply.setAttachmentUrl(dto.getAttachmentUrl());
        reply.setAttachmentName(dto.getAttachmentName());

        System.out.println("??? About to save reply with content: '" + reply.getContent() + "'");
        DiscussionReply saved = discussionReplyRepository.save(reply);
        
        return convertToDTO(saved);
    }

    public List<DiscussionReplyDTO> getRepliesByDiscussionId(Integer discussionId) {
        List<DiscussionReply> replies = discussionReplyRepository.findByDiscussionIdOrderByCreatedAt(discussionId);
        return replies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Long getReplyCount(Integer discussionId) {
        return discussionReplyRepository.countByDiscussionId(discussionId);
    }

    public boolean deleteReply(Integer replyId) {
        Optional<DiscussionReply> replyOpt = discussionReplyRepository.findById(replyId);
        if (replyOpt.isPresent()) {
            discussionReplyRepository.delete(replyOpt.get());
            return true;
        }
        return false;
    }

    public boolean deleteReply(Integer replyId, Integer userId, boolean isAdmin) {
        Optional<DiscussionReply> replyOpt = discussionReplyRepository.findById(replyId);
        if (replyOpt.isEmpty()) return false;
        
        DiscussionReply reply = replyOpt.get();
        
        // Admin can delete any reply, others can only delete their own
        if (!isAdmin && !reply.getUser().getUserId().equals(userId)) {
            return false;
        }
        
        discussionReplyRepository.deleteById(replyId);
        return true;
    }

    private DiscussionReplyDTO convertToDTO(DiscussionReply reply) {
        DiscussionReplyDTO dto = new DiscussionReplyDTO();
        dto.setReplyId(reply.getReplyId());
        dto.setDiscussionId(reply.getDiscussion().getId()); // Sử dụng getId() thay vì getDiscussionId()
        dto.setUserId(reply.getUser().getUserId());
        dto.setUserName(reply.getUser().getFullName());
        dto.setUserRole(reply.getUser().getRole().toString()); // Convert enum to string
        dto.setUserAvatar(reply.getUser().getAvatarUrl());
        dto.setContent(reply.getContent());
        dto.setParentReplyId(reply.getParentReplyId()); // Add parent reply ID
        dto.setAttachmentUrl(reply.getAttachmentUrl());
        dto.setAttachmentName(reply.getAttachmentName());
        dto.setCreatedAt(reply.getCreatedAt());
        return dto;
    }
}
