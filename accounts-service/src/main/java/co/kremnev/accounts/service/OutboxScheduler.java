package co.kremnev.accounts.service;

import co.kremnev.accounts.repository.OutboxRepository;
import co.kremnev.starter.KafkaNotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaNotificationProducer notificationProducer;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processOutbox() {
        var events = outboxRepository.findBySentFalseOrderByCreatedAtAsc();
        for (var event : events) {
            try {
                notificationProducer.send(event.getLogin(), event.getMessage());
                event.setSent(true);
                outboxRepository.save(event);
            } catch (Exception e) {
                log.warn("Failed to send outbox event {}: {}", event.getId(), e.getMessage());
                break;
            }
        }
    }
}
