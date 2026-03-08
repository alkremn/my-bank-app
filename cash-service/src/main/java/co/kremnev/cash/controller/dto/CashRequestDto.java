package co.kremnev.cash.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashRequestDto {
    private BigDecimal amount;
}
