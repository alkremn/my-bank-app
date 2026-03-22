package co.kremnev.front.service;

import org.springframework.stereotype.Service;
import co.kremnev.front.client.AccountApiClient;
import co.kremnev.front.client.CashApiClient;
import co.kremnev.front.client.TransferApiClient;
import co.kremnev.front.client.dto.AccountResponse;
import co.kremnev.front.controller.dto.CashAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankService {

    private final AccountApiClient accountApiClient;
    private final CashApiClient cashApiClient;
    private final TransferApiClient transferApiClient;

    public BankService(AccountApiClient accountApiClient,
                       CashApiClient cashApiClient,
                       TransferApiClient transferApiClient) {
        this.accountApiClient = accountApiClient;
        this.cashApiClient = cashApiClient;
        this.transferApiClient = transferApiClient;
    }

    public AccountResponse getAccount(String login) {
        return accountApiClient.getByLogin(login);
    }

    public List<AccountResponse> getOtherAccounts(String login) {
        return accountApiClient.getAll().stream()
                .filter(a -> !a.login().equals(login))
                .toList();
    }

    public void updateAccount(String login, String name, LocalDate birthdate) {
        List<String> errors = validateAccount(name, birthdate);
        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join("; ", errors));
        }
        accountApiClient.update(login, name, birthdate);
    }

    public void processCash(String login, int value, CashAction action) {
        BigDecimal amount = BigDecimal.valueOf(value);
        if (action == CashAction.PUT) {
            cashApiClient.deposit(amount);
        } else {
            cashApiClient.withdraw(amount);
        }
    }

    public void processTransfer(String login, String targetLogin, int value) {
        transferApiClient.transfer(targetLogin, BigDecimal.valueOf(value));
    }

    private List<String> validateAccount(String name, LocalDate birthdate) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("Имя не может быть пустым");
        }
        if (birthdate == null) {
            errors.add("Дата рождения обязательна");
        } else if (ChronoUnit.YEARS.between(birthdate, LocalDate.now()) < 18) {
            errors.add("Возраст должен быть не менее 18 лет");
        }
        return errors;
    }
}
