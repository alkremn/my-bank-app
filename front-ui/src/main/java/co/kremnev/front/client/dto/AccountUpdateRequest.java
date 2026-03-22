package co.kremnev.front.client.dto;

import java.time.LocalDate;

public record AccountUpdateRequest(String login, String name, LocalDate birthdate) {
}
