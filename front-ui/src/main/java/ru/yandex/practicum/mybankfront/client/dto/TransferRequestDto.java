package ru.yandex.practicum.mybankfront.client.dto;

import java.math.BigDecimal;

public record TransferRequestDto(String toLogin, BigDecimal amount) {
}
