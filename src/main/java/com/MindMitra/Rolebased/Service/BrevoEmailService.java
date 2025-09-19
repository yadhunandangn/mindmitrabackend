package com.MindMitra.Rolebased.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BrevoEmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String URL = "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String to, String subject, String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", "MINDMITRA", "email", "yadhunandan2002@gmail.com"));
        body.put("to", List.of(Map.of("email", to)));
        body.put("subject", subject);
        body.put("htmlContent", "<html><body>" + content + "</body></html>");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.exchange(URL, HttpMethod.POST, request, String.class);
    }
}

