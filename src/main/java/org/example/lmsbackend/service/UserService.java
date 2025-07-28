package org.example.lmsbackend.service;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.lmsbackend.dto.UserDTO;
import org.example.lmsbackend.email.EmailService;
import org.example.lmsbackend.model.User;
import org.example.lmsbackend.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // ✅ Đăng nhập
    public boolean login(UserDTO userDTO) {
        User user = userMapper.findByUsername(userDTO.getUsername());
        if (user == null) return false;

        if (!user.isVerified()) {
            throw new RuntimeException("Tài khoản chưa được xác minh");
        }

        return passwordEncoder.matches(userDTO.getPassword(), user.getPassword());
    }

    // ✅ Đăng ký (có avatar)
    public boolean register(UserDTO userDTO, MultipartFile avatarFile) {
        if (userMapper.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userMapper.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());

        try {
            User.Role role = User.Role.valueOf(userDTO.getRole().toLowerCase());
            user.setRole(role);

            if (role == User.Role.instructor) {
                if (!StringUtils.hasText(userDTO.getCvUrl())) {
                    throw new RuntimeException("CV is required for instructor registration.");
                }
                user.setCvUrl(userDTO.getCvUrl());
                user.setVerified(false);
            } else {
                user.setVerified(userDTO.getIsVerified() != null ? userDTO.getIsVerified() : true);
            }

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + userDTO.getRole());
        }

        // ✅ Lưu avatar nếu có
        if (avatarFile != null && !avatarFile.isEmpty()) {
            user.setAvatarUrl(saveAvatar(avatarFile));
        } else {
            user.setAvatarUrl("/uploads/avatars/default.png");
        }

        try {
            return userMapper.insertUser(user) > 0;
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Username or Email already exists (DB constraint)");
        }
    }
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setAvatarUrl(ensureDefaultAvatar(user.getAvatarUrl()));
        }
        return user;
    }
    // ✅ Cập nhật người dùng
    public boolean updateUser(Long id, UserDTO userDTO, MultipartFile avatarFile) {
        User existingUser = userMapper.findById(id);
        if (existingUser == null) return false;

        existingUser.setUserId(id.intValue());
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFullName(userDTO.getFullName());

        try {
            existingUser.setRole(User.Role.valueOf(userDTO.getRole().toLowerCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + userDTO.getRole());
        }

        if (userDTO.getIsVerified() != null) {
            existingUser.setVerified(userDTO.getIsVerified());
        }

        // ✅ Chỉ cập nhật mật khẩu nếu có nhập
        if (StringUtils.hasText(userDTO.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword().trim()));
        }

        // ✅ Cập nhật avatar nếu có
        if (avatarFile != null && !avatarFile.isEmpty()) {
            existingUser.setAvatarUrl(saveAvatar(avatarFile));
        } else if (existingUser.getAvatarUrl() == null || existingUser.getAvatarUrl().trim().isEmpty()) {
            // ✅ Nếu chưa có avatar, sử dụng avatar mặc định
            existingUser.setAvatarUrl("/uploads/avatars/default.png");
        }

        // ✅ Cập nhật CV nếu có
        if (StringUtils.hasText(userDTO.getCvUrl())) {
            existingUser.setCvUrl(userDTO.getCvUrl());
        }

        return userMapper.updateUser(existingUser) > 0;
    }

    // ✅ Lưu file avatar
    private String saveAvatar(MultipartFile file) {
        try {
            String uploadDir = "uploads/avatars";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String cleanedFilename = originalFilename != null ? originalFilename.replaceAll("\\s+", "_") : "avatar.png";
            String filename = UUID.randomUUID() + "_" + cleanedFilename;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/avatars/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file avatar", e);
        }
    }

    // ✅ Đảm bảo avatar URL luôn có giá trị mặc định
    private String ensureDefaultAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return "/uploads/avatars/default.png";
        }
        return avatarUrl;
    }

    // ✅ Lấy danh sách người dùng theo điều kiện
    public List<User> getUsers(Integer userId, String role, Boolean isVerified, String username) {
        List<User> users = userMapper.findUsersByConditions(userId, role, isVerified, username);
        // ✅ Đảm bảo tất cả user đều có avatar mặc định
        users.forEach(user -> {
            user.setAvatarUrl(ensureDefaultAvatar(user.getAvatarUrl()));
        });
        return users;
    }

    // ✅ Xóa người dùng
    public boolean deleteUser(int id) {
        return userMapper.deleteUserById(id) > 0;
    }
}
