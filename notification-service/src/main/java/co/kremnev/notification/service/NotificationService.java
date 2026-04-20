package co.kremnev.notification.service;

import co.kremnev.notification.model.Notification;
import co.kremnev.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification create(String login, String message) {
        log.debug("Saving notification: login={}", login);
        var notification = new Notification(null, login, message, LocalDateTime.now());
        var saved = notificationRepository.save(notification);
        log.info("Notification saved: login={}, id={}", login, saved.getId());
        return saved;
    }

    public List<Notification> getByLogin(String login) {
        log.debug("Fetching notifications for login={}", login);
        return notificationRepository.findByLoginOrderByCreatedAtDesc(login);
    }
}
