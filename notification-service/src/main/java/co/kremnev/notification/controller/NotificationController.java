package co.kremnev.notification.controller;

import co.kremnev.notification.controller.dto.NotificationRequestDto;
import co.kremnev.notification.model.Notification;
import co.kremnev.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public Notification create(@RequestBody NotificationRequestDto request) {
        return notificationService.create(request.getLogin(), request.getMessage());
    }

    @GetMapping("/{login}")
    public List<Notification> getByLogin(@PathVariable String login) {
        return notificationService.getByLogin(login);
    }
}
