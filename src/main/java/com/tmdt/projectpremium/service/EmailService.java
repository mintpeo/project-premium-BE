package com.tmdt.projectpremium.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String fullName, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ProjectPremium - Mã xác nhận đăng ký");
        message.setText(
            "Xin chào " + fullName + ",\n\n" +
            "Mã xác nhận đăng ký tài khoản của bạn là:\n\n" +
            "  " + otp + "\n\n" +
            "Mã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ ProjectPremium"
        );
        mailSender.send(message);
    }

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

    public void sendResetPasswordEmail(String toEmail, String fullName, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ProjectPremium - Đặt lại mật khẩu");
        message.setText(
            "Xin chào " + fullName + ",\n\n" +
            "Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu của bạn.\n\n" +
            "Mật khẩu mới của bạn là:\n" +
            "  " + newPassword + "\n\n" +
            "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi nhận được email này.\n\n" +
            "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ ProjectPremium"
        );
        mailSender.send(message);
    }
}
