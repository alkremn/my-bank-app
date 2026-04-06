package co.kremnev.notification.listener;

import co.kremnev.notification.service.NotificationService;
import co.kremnev.starter.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${my-bank.notification.topic:notifications}")
    public void onNotification(NotificationEvent event, Acknowledgment acknowledgment) {
        log.info("Received notification for {}: {}", event.login(), event.message());
        notificationService.create(event.login(), event.message());
        acknowledgment.acknowledge();
    }
}
