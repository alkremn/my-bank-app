package co.kremnev.notification.service;

import co.kremnev.notification.model.Notification;
import co.kremnev.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void create_savesNotification() {
        var saved = new Notification(1L, "ivanov", "Deposit: +500", LocalDateTime.now());
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        var result = notificationService.create("ivanov", "Deposit: +500");

        assertEquals("ivanov", result.getLogin());
        assertEquals("Deposit: +500", result.getMessage());
        assertNotNull(result.getCreatedAt());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getByLogin_returnsSortedList() {
        var now = LocalDateTime.now();
        var notifications = List.of(
                new Notification(2L, "ivanov", "Transfer: -300", now),
                new Notification(1L, "ivanov", "Deposit: +500", now.minusHours(1))
        );
        when(notificationRepository.findByLoginOrderByCreatedAtDesc("ivanov")).thenReturn(notifications);

        var result = notificationService.getByLogin("ivanov");

        assertEquals(2, result.size());
        assertEquals("Transfer: -300", result.get(0).getMessage());
        assertEquals("Deposit: +500", result.get(1).getMessage());
    }

    @Test
    void getByLogin_noNotifications_returnsEmptyList() {
        when(notificationRepository.findByLoginOrderByCreatedAtDesc("unknown")).thenReturn(List.of());

        var result = notificationService.getByLogin("unknown");

        assertTrue(result.isEmpty());
    }
}
