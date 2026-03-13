package com.techgear.store.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAccountInfo(String toEmail, String fullName, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("TechGear Store - Tài khoản đăng nhập");
        message.setText(
                "Xin chào " + fullName + ",\n\n" +
                        "Tài khoản của bạn đã được tạo trên hệ thống TechGear Store.\n\n" +
                        "Email đăng nhập: " + toEmail + "\n" +
                        "Mật khẩu tạm: " + tempPassword + "\n\n" +
                        "Vui lòng đăng nhập và đổi mật khẩu sớm nhất có thể.\n\n" +
                        "Trân trọng,\nTechGear Store"
        );

        mailSender.send(message);
    }
}