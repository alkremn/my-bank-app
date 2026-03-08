package co.kremnev.notification.controller.dto;

import lombok.Data;

@Data
public class NotificationRequestDto {
    private String login;
    private String message;
}
