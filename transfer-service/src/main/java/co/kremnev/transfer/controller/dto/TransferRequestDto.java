package co.kremnev.transfer.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDto {
    private String fromLogin;
    private String toLogin;
    private BigDecimal amount;
}
