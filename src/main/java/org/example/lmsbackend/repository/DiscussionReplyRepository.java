package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, Integer> {
    
    @Query("SELECT dr FROM DiscussionReply dr WHERE dr.discussion.discussionId = :discussionId ORDER BY dr.createdAt ASC")
    List<DiscussionReply> findByDiscussionIdOrderByCreatedAt(@Param("discussionId") Integer discussionId);
    
    @Query("SELECT dr FROM DiscussionReply dr WHERE dr.user.userId = :userId ORDER BY dr.createdAt DESC")
    List<DiscussionReply> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(dr) FROM DiscussionReply dr WHERE dr.discussion.discussionId = :discussionId")
    Long countByDiscussionId(@Param("discussionId") Integer discussionId);
}
