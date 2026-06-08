package com.iepca.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:5000}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("IEP Continental Americano - Restablecer Contrasena");
        message.setText("Hola,\n\n"
                + "Hemos recibido una solicitud para restablecer tu contrasena.\n\n"
                + "Haz clic en el siguiente enlace para crear una nueva contrasena:\n"
                + resetLink + "\n\n"
                + "Este enlace expirara en 1 hora.\n\n"
                + "Si no solicitaste este cambio, puedes ignorar este correo.\n\n"
                + "Saludos,\nIEP Continental Americano");

        mailSender.send(message);
        logger.info("Email de restablecimiento enviado a: {}", to);
    }

    public void sendWelcomeEmail(String to, String name, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Bienvenido a IEP Continental Americano");
        message.setText("Hola " + name + ",\n\n"
                + "Tu cuenta en IEP Continental Americano ha sido creada exitosamente.\n\n"
                + "Tus credenciales de acceso son:\n"
                + "Email: " + to + "\n"
                + "Contrasena temporal: " + temporaryPassword + "\n\n"
                + "Por favor, cambia tu contrasena despues de iniciar sesion.\n\n"
                + "Saludos,\nIEP Continental Americano");

        mailSender.send(message);
        logger.info("Email de bienvenida enviado a: {}", to);
    }
}

