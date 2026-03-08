package ru.yandex.practicum.mybankfront.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponse(Long id, String login, String name, LocalDate birthdate, BigDecimal balance) {
}
