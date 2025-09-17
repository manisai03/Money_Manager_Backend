package com.example.money_mhttps.start.spring.io.anager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    /**
     * Send a simple email (no attachments)
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("email", "manisaireddysomala@gmail.com"),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", "<p>" + body + "</p>"
            );

            sendRequest(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send an email with an attachment
     */
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String filename) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("email", "manisaireddysomala@gmail.com"),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", "<p>" + body + "</p>",
                    "attachment", List.of(
                            Map.of(
                                    "name", filename,
                                    "content", Base64.getEncoder().encodeToString(attachment)
                            )
                    )
            );

            sendRequest(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email with attachment: " + e.getMessage(), e);
        }
    }

    private void sendRequest(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(BREVO_URL, request, String.class);
    }
}
