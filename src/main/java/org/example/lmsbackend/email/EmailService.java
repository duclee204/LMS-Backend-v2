package org.example.lmsbackend.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendAccountInfo(String toEmail, String username, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("Thông tin tài khoản đăng ký thành công");
        message.setText("Xin chào " + username + ",\n\n"
                + "Bạn đã đăng ký tài khoản thành công.\n"
                + "Tài khoản: " + toEmail + "\n"
                + "Mật khẩu: " + rawPassword + "\n\n"
                + "Vui lòng đổi mật khẩu ngay sau lần đăng nhập đầu tiên.\n\n"
                + "Trân trọng.");
        mailSender.send(message);
    }
}
