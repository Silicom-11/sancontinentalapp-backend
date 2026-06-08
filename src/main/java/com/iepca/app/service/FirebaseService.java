package com.iepca.app.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    @Value("${firebase.project-id:}")
    private String projectId;

    @Value("${firebase.private-key:}")
    private String privateKey;

    @Value("${firebase.client-email:}")
    private String clientEmail;

    private boolean configured = false;

    @PostConstruct
    public void init() {
        if (projectId == null || projectId.isBlank()
                || privateKey == null || privateKey.isBlank()
                || clientEmail == null || clientEmail.isBlank()) {
            logger.warn("Firebase no configurado - notificaciones push deshabilitadas");
            return;
        }

        try {
            String serviceAccountJson = String.format(
                    "{\"type\":\"service_account\",\"project_id\":\"%s\",\"private_key\":\"%s\",\"client_email\":\"%s\"}",
                    projectId, privateKey.replace("\n", "\\n"), clientEmail);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8))))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            configured = true;
            logger.info("Firebase inicializado exitosamente");
        } catch (Exception e) {
            logger.error("Error al inicializar Firebase: {}", e.getMessage());
        }
    }

    public void sendToDevice(String token, String title, String body, Map<String, String> data) {
        if (!configured) return;

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data != null ? data : Map.of())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("NotificaciÃ³n push enviada: {}", response);
        } catch (Exception e) {
            logger.error("Error enviando push a dispositivo: {}", e.getMessage());
        }
    }

    public void sendToMultipleDevices(List<String> tokens, String title, String body, Map<String, String> data) {
        if (!configured || tokens == null || tokens.isEmpty()) return;

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data != null ? data : Map.of())
                    .build();

            var response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            logger.info("Push multicast: {} Ã©xitos, {} fallos",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (Exception e) {
            logger.error("Error enviando push multicast: {}", e.getMessage());
        }
    }

    public void notifyParentOfDisconnection(List<String> tokens, String studentName, double latitude, double longitude) {
        sendToMultipleDevices(tokens,
                "Estudiante desconectado",
                studentName + " se ha desconectado del seguimiento GPS",
                Map.of("type", "disconnection", "studentName", studentName,
                        "latitude", String.valueOf(latitude), "longitude", String.valueOf(longitude)));
    }

    public void notifyNewGrade(List<String> tokens, String studentName, String subject, String grade) {
        sendToMultipleDevices(tokens,
                "Nueva calificaciÃ³n",
                studentName + " ha recibido una nota en " + subject + ": " + grade,
                Map.of("type", "grade", "studentName", studentName, "subject", subject, "grade", grade));
    }

    public void notifyAbsence(List<String> tokens, String studentName, String date) {
        sendToMultipleDevices(tokens,
                "Inasistencia registrada",
                studentName + " tiene una inasistencia registrada el " + date,
                Map.of("type", "absence", "studentName", studentName, "date", date));
    }

    public void notifyNewMessage(String token, String senderName, String preview) {
        sendToDevice(token,
                "Nuevo mensaje de " + senderName,
                preview,
                Map.of("type", "message", "senderName", senderName));
    }

    public void notifyUpcomingEvent(List<String> tokens, String eventTitle, String eventDate) {
        sendToMultipleDevices(tokens,
                "Evento prÃ³ximo",
                eventTitle + " - " + eventDate,
                Map.of("type", "event", "eventTitle", eventTitle, "eventDate", eventDate));
    }
}

