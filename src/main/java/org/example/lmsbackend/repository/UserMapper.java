package org.example.lmsbackend.repository;

import org.example.lmsbackend.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import java.util.List;

@Mapper
public interface UserMapper {

    // 🔍 Tìm người dùng theo username
    @Select("""
        SELECT 
            user_id AS userId,
            username,
            password,
            email,
            full_name AS fullName,
            role,
            is_verified AS isVerified,
            verified_at AS verifiedAt,
            created_at AS createdAt,
            updated_at AS updatedAt,
            cv_url AS cvUrl,
            avatar_url AS avatarUrl
        FROM users
        WHERE username = #{username}
    """)
    User findByUsername(String username);

    // 🔍 Tìm người dùng theo ID
    @Select("""
        SELECT 
            user_id AS userId,
            username,
            password,
            email,
            full_name AS fullName,
            role,
            is_verified AS isVerified,
            verified_at AS verifiedAt,
            created_at AS createdAt,
            updated_at AS updatedAt,
            cv_url AS cvUrl,
            avatar_url AS avatarUrl
        FROM users
        WHERE user_id = #{id}
    """)
    User findById(@Param("id") Long id);

    // 🔍 Tìm người dùng theo User ID (Integer)
    @Select("""
        SELECT 
            user_id AS userId,
            username,
            password,
            email,
            full_name AS fullName,
            role,
            is_verified AS isVerified,
            verified_at AS verifiedAt,
            created_at AS createdAt,
            updated_at AS updatedAt,
            cv_url AS cvUrl,
            avatar_url AS avatarUrl
        FROM users
        WHERE user_id = #{userId}
    """)
    User findByUserId(@Param("userId") Integer userId);

    // ➕ Thêm người dùng mới
    @Insert("""
        INSERT INTO users (
            username, password, email, full_name, role, is_verified, 
            verified_at, cv_url, avatar_url
        )
        VALUES (
            #{username}, #{password}, #{email}, #{fullName}, #{role}, #{isVerified},
            CASE WHEN #{isVerified} = TRUE THEN NOW() ELSE NULL END,
            #{cvUrl}, #{avatarUrl}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int insertUser(User user);

    // 📋 Lấy danh sách người dùng có điều kiện
    @Select("""
    <script>
        SELECT 
            user_id AS userId,
            username,
            password,
            email,
            full_name AS fullName,
            role,
            verification_token AS verificationToken,
            is_verified AS isVerified,
            verified_at AS verifiedAt,
            created_at AS createdAt,
            updated_at AS updatedAt,
            cv_url AS cvUrl,
            avatar_url AS avatarUrl
        FROM users
        <where>
            <if test="userId != null">
                AND user_id = #{userId}
            </if>
            <if test="role != null and role != ''">
                AND role = #{role}
            </if>
            <if test="isVerified != null">
                AND is_verified = #{isVerified}
            </if>
            <if test="username != null and username != ''">
                AND username LIKE CONCAT('%', #{username}, '%')
            </if>
        </where>
    </script>
    """)
    @Lang(XMLLanguageDriver.class)
    List<User> findUsersByConditions(@Param("userId") Integer userId,
                                     @Param("role") String role,
                                     @Param("isVerified") Boolean isVerified,
                                     @Param("username") String username);


    // 🔄 Cập nhật người dùng (cập nhật avatar luôn nếu cần)
    @Update("""
        UPDATE users SET
            username = #{username},
            password = #{password},
            email = #{email},
            full_name = #{fullName},
            role = #{role},
            is_verified = #{isVerified},
            avatar_url = #{avatarUrl},
            cv_url = #{cvUrl}
        WHERE user_id = #{userId}
    """)
    int updateUser(User user);

    // ❌ Xóa người dùng
    @Delete("DELETE FROM users WHERE user_id = #{id}")
    int deleteUserById(@Param("id") int id);

    // ✅ Kiểm tra tồn tại username
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);

    // ✅ Kiểm tra tồn tại email
    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(String email);
}
