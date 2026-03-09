package co.kremnev.front.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponse(Long id, String login, String name, LocalDate birthdate, BigDecimal balance) {
}
