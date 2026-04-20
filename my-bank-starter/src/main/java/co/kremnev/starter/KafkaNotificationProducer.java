package co.kremnev.starter;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaNotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    private final MeterRegistry meterRegistry;

    @SuppressWarnings("unchecked")
    public KafkaNotificationProducer(KafkaTemplate<String, ?> kafkaTemplate, String topic,
                                     MeterRegistry meterRegistry) {
        this.kafkaTemplate = (KafkaTemplate<String, Object>) kafkaTemplate;
        this.topic = topic;
        this.meterRegistry = meterRegistry;
    }

    public void send(String login, String message) {
        try {
            kafkaTemplate.send(topic, login, new NotificationEvent(login, message));
        } catch (Exception e) {
            log.warn("Failed to send notification for {}: {}", login, e.getMessage());
            if (meterRegistry != null) {
                meterRegistry.counter("bank.notification.send.failed", "login", login).increment();
            }
        }
    }
}
