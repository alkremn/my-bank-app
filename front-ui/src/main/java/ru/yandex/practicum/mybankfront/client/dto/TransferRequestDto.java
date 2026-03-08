package ru.yandex.practicum.mybankfront.client.dto;

import java.math.BigDecimal;

public record TransferRequestDto(String fromLogin, String toLogin, BigDecimal amount) {
}
