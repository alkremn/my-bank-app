package co.kremnev.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaNotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationProducer.class);

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final String topic;

    public KafkaNotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(String login, String message) {
        try {
            kafkaTemplate.send(topic, login, new NotificationEvent(login, message));
        } catch (Exception e) {
            log.warn("Failed to send notification for {}: {}", login, e.getMessage());
        }
    }
}
