package co.kremnev.accounts.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceUpdateDto {
    private BigDecimal amount;
}
