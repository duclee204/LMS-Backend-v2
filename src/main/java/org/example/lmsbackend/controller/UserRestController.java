package org.example.lmsbackend.controller;

import org.example.lmsbackend.model.User;
import org.example.lmsbackend.dto.UserDTO;
import org.example.lmsbackend.security.CustomUserDetails;
import org.example.lmsbackend.service.FileStorageService;
import org.example.lmsbackend.service.UserService;
import org.example.lmsbackend.utils.JwtTokenUtil;
import org.example.lmsbackend.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private FileStorageService fileStorageService;

        // ✅ API đăng nhập
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
            try {
                boolean success = userService.login(userDTO);
                if (!success) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Tên đăng nhập hoặc mật khẩu không đúng"));
                }

                User user = userMapper.findByUsername(userDTO.getUsername());
                String token = jwtTokenUtil.generateToken(user);

                return ResponseEntity.ok(Map.of(
                        "message", "Đăng nhập thành công",
                        "token", token
                ));
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
            }
        }

        // ✅ API đăng ký
        @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> register(
                @RequestParam String username,
                @RequestParam String password,
                @RequestParam String email,
                @RequestParam String fullName,
                @RequestParam(required = false) String role,
                @RequestPart(value = "cv", required = false) MultipartFile cvFile,
                @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
        ) {
            try {
                if (role == null || role.isBlank()) role = "student";
                boolean isVerified = !"instructor".equalsIgnoreCase(role);

                // CV xử lý riêng
                String cvPath = null;
                if ("instructor".equalsIgnoreCase(role)) {
                    if (cvFile == null || cvFile.isEmpty()) {
                        return ResponseEntity.badRequest().body(Map.of("message", "CV file is required for instructors"));
                    }
                    cvPath = fileStorageService.saveFile(cvFile, "cvs");
                }

                // Avatar xử lý
                String avatarPath = avatarFile != null && !avatarFile.isEmpty()
                        ? fileStorageService.saveFile(avatarFile, "avatars")
                        : "/uploads/avatars/default.png";

                UserDTO dto = new UserDTO(username, password, email, fullName, role, isVerified, cvPath, avatarPath);

                boolean created = userService.register(dto, avatarFile);
                if (created) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "User registered successfully");
                    if (cvPath != null) response.put("cvUrl", cvPath);
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Registration failed"));
                }

            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
            }
        }

        // ✅ API cập nhật người dùng (hỗ trợ avatar)
        @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> updateUser(
                @PathVariable Long id,
                @RequestParam String username,
                @RequestParam String email,
                @RequestParam String fullName,
                @RequestParam String role,
                @RequestParam(required = false) String password,
                @RequestParam(required = false) Boolean isVerified,
                @RequestParam(required = false) String cvUrl,
                @RequestPart(required = false) MultipartFile avatar
        ) {
            try {
                String avatarUrl = null;
                if (avatar != null && !avatar.isEmpty()) {
                    avatarUrl = fileStorageService.saveFile(avatar, "avatars");
                }

                // Xử lý: nếu password rỗng hoặc null → bỏ qua
                String safePassword = (password != null && !password.isBlank()) ? password : null;

                UserDTO dto = new UserDTO(username, safePassword, email, fullName, role, isVerified, cvUrl, avatarUrl);
                boolean updated = userService.updateUser(id, dto, avatar);

                if (updated) {
                    return ResponseEntity.ok(Map.of("message", "User updated successfully"));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Update failed: " + e.getMessage()));
            }
        }

    // ✅ API danh sách người dùng
    // ✅ API lấy danh sách người dùng theo điều kiện
    @GetMapping("/list")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) String username) {

        List<User> users = userService.getUsers(userId, role, isVerified, username);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'instructor', 'student')")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        try {
            // ✅ Nếu người dùng không phải admin → chỉ được xem chính họ
            if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"))
                    && !currentUser.getUserId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied"));
            }

            User user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error retrieving user"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userService.getUserById(Long.valueOf(userDetails.getUserId()));
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error retrieving profile"));
        }
    }

    // ✅ API xóa người dùng
        @DeleteMapping("/delete/{id}")
        @PreAuthorize("hasRole('admin')")
        public ResponseEntity<?> deleteUser(@PathVariable int id) {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Xóa người dùng thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Người dùng không tồn tại"));
            }
        }

    }
