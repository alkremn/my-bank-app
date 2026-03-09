package co.kremnev.front.client.dto;

import java.math.BigDecimal;

public record TransferRequestDto(String toLogin, BigDecimal amount) {
}
