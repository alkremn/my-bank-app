package co.kremnev.notification.service;

import co.kremnev.notification.model.Notification;
import co.kremnev.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification create(String login, String message) {
        var notification = new Notification(null, login, message, LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public List<Notification> getByLogin(String login) {
        return notificationRepository.findByLoginOrderByCreatedAtDesc(login);
    }
}
