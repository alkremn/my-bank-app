package ru.yandex.practicum.mybankfront.client.dto;

import java.math.BigDecimal;

public record CashRequestDto(String login, BigDecimal amount) {
}
