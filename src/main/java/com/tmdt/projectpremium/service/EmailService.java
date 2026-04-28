package com.tmdt.projectpremium.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordEmail(String toEmail, String fullName, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Chào mừng bạn đến với ProjectPremium - Thông tin tài khoản");
        message.setText(
            "Xin chào " + fullName + ",\n\n" +
            "Tài khoản của bạn đã được tạo thành công!\n\n" +
            "Thông tin đăng nhập:\n" +
            "  Email: " + toEmail + "\n" +
            "  Mật khẩu: " + password + "\n\n" +
            "Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ ProjectPremium"
        );
        mailSender.send(message);
    }
}
