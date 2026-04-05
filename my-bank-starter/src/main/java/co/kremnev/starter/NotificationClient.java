package co.kremnev.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestClient.Builder restClientBuilder;
    private final String notificationUrl;

    public NotificationClient(RestClient.Builder restClientBuilder, String notificationUrl) {
        this.restClientBuilder = restClientBuilder;
        this.notificationUrl = notificationUrl;
    }

    public void send(String login, String message) {
        try {
            restClientBuilder.build()
                    .post()
                    .uri(notificationUrl + "/notifications")
                    .body(Map.of("login", login, "message", message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to send notification for {}: {}", login, e.getMessage());
        }
    }
}
