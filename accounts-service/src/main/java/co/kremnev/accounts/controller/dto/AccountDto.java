package co.kremnev.accounts.controller.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountDto {
    private String login;
    private String name;
    private LocalDate birthdate;
}
