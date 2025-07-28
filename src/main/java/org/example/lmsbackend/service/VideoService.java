package org.example.lmsbackend.service;

import org.example.lmsbackend.dto.VideoDTO;
import org.example.lmsbackend.utils.VideoMapperUtil;
import org.example.lmsbackend.model.Video;
import org.example.lmsbackend.model.Course;
import org.example.lmsbackend.model.User;
import org.example.lmsbackend.repository.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private VideoMapper videoMapper;

    @Autowired
    public VideoService(VideoMapper videoMapper) {
        this.videoMapper = videoMapper;
    }

    public int createVideo(Video video) {
        return videoMapper.insertVideo(video);
    }

    public List<VideoDTO> getAllVideos(String title) {
        List<Video> videos = videoMapper.findVideos(title);
        return videos.stream()
                .map(VideoMapperUtil::toDTO)
                .collect(Collectors.toList());
    }

    public VideoDTO getVideoById(Long id) {
        Video video = videoMapper.findById(id);
        return VideoMapperUtil.toDTO(video);
    }

    public int deleteVideo(Long videoId) {
        return videoMapper.deleteVideo(videoId);
    }

    public List<VideoDTO> getVideosByCourse(Integer courseId) {
        List<Video> videos = videoMapper.findVideosByCourseId(courseId);
        return videos.stream()
                .map(VideoMapperUtil::toDTO)
                .collect(Collectors.toList());
    }

    public VideoDTO uploadVideo(MultipartFile file, String title, String description, Integer courseId, Integer instructorId) {
        try {
            String fileUrl = saveFile(file);
            if (fileUrl == null) return null;
            
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setFileUrl(fileUrl);
            video.setFileSize(file.getSize());
            video.setMimeType(file.getContentType());
            
            // Set course và instructor
            Course course = new Course();
            course.setCourseId(courseId);
            video.setCourse(course);
            
            User instructor = new User();
            instructor.setUserId(instructorId);
            video.setInstructor(instructor);
            
            videoMapper.insertVideo(video);
            return VideoMapperUtil.toDTO(video);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int updateVideo(Video video) {
        return videoMapper.updateVideo(video);
    }

    public boolean isInstructorOfVideo(Long videoId, Integer instructorId) {
        System.out.println("🔍 Checking instructor access: videoId=" + videoId + ", instructorId=" + instructorId);
        boolean result = videoMapper.isInstructorOfVideo(videoId, instructorId);
        System.out.println("🔍 Instructor access result: " + result);
        return result;
    }

    public boolean canStudentAccessVideo(Long videoId, Integer userId) {
        return videoMapper.canStudentAccessVideo(videoId, userId);
    }

    public Resource getVideoResource(Long videoId) {
        try {
            Video video = videoMapper.findById(videoId);
            if (video == null) {
                System.out.println("❌ Video not found in DB: " + videoId);
                return null;
            }
            
            System.out.println("🎬 Video from DB: id=" + video.getVideoId() + ", fileUrl=" + video.getFileUrl());
            
            if (video.getFileUrl() == null) {
                System.out.println("❌ FileUrl is null for video: " + videoId);
                return null;
            }
            
            // video.getFileUrl() = "/videos/filename.mp4"
            // Extract just the filename from the fileUrl
            String fileName = video.getFileUrl().substring(video.getFileUrl().lastIndexOf("/") + 1);
            
            // Construct correct path: uploads/videos/filename.mp4
            Path filePath = Paths.get("uploads/videos/" + fileName);
            System.out.println("🎯 Looking for file at: " + filePath.toAbsolutePath());
            
            if (!Files.exists(filePath)) {
                System.out.println("❌ File does not exist at: " + filePath.toAbsolutePath());
                return null;
            }
            
            System.out.println("✅ File found, creating resource");
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                System.out.println("✅ Resource is readable");
                return resource;
            } else {
                System.out.println("❌ Resource exists but not readable");
                return null;
            }
        } catch (Exception e) {
            System.out.println("❌ Exception in getVideoResource: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/videos");
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            return "/videos/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
