package org.example.lmsbackend.utils;
import org.example.lmsbackend.dto.VideoDTO;
import org.example.lmsbackend.model.Video;
public class VideoMapperUtil {
    public static VideoDTO toDTO(Video video) {
        if (video == null) return null;

        VideoDTO dto = new VideoDTO();
        dto.setVideoId(video.getVideoId());
        dto.setTitle(video.getTitle());
        dto.setDescription(video.getDescription());
        dto.setFileUrl(video.getFileUrl());
        dto.setDuration(video.getDuration());
        dto.setFileSize(video.getFileSize());
        dto.setMimeType(video.getMimeType());
        dto.setUploadedAt(video.getUploadedAt());
        
        // Thêm thông tin course và instructor
        if (video.getCourse() != null) {
            dto.setCourseId(video.getCourse().getCourseId());
            dto.setCourseName(video.getCourse().getTitle());
        }
        
        if (video.getInstructor() != null) {
            dto.setInstructorId(video.getInstructor().getUserId());
            dto.setInstructorName(video.getInstructor().getFullName());
        }
        
        return dto;
    }    
}
