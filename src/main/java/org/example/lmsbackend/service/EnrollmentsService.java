package org.example.lmsbackend.service;

import org.example.lmsbackend.dto.EnrollmentsDTO;
import org.example.lmsbackend.repository.EnrollmentsMapper;
import org.example.lmsbackend.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EnrollmentsService {
    public List<Integer> getEnrolledCourseIds(int userId) {
        return enrollmentMapper.getEnrolledCourseIdsByUserId(userId);
    }
    @Autowired
    private EnrollmentsMapper enrollmentMapper;

    public boolean enrollUserInCourse(int userId, int courseId) {
        int count = enrollmentMapper.countEnrollment(userId, courseId);
        if (count > 0) {
            return false;
        }
        enrollmentMapper.enrollCourse(userId, courseId);
        return true;
    }

    public List<EnrollmentsDTO> getEnrolledCourses(int userId) {
        return enrollmentMapper.getEnrolledCoursesByUserId(userId);
    }

    public int deleteEnrollment(int userId, int courseId) {
        return enrollmentMapper.deleteEnrollment(userId, courseId);
    }

    public List<UserDTO> getEnrolledUsersByCourse(int courseId) {
        return enrollmentMapper.getUsersByCourseId(courseId);
    }
    public boolean isStudentEnrolled(int userId, int courseId) {
        return enrollmentMapper.countEnrollment(userId, courseId) > 0;
    }
}