package co.kremnev.accounts.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDto {
    private String fromLogin;
    private String toLogin;
    private BigDecimal amount;
}
