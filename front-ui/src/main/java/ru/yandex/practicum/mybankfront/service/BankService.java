package ru.yandex.practicum.mybankfront.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ru.yandex.practicum.mybankfront.client.AccountApiClient;
import ru.yandex.practicum.mybankfront.client.CashApiClient;
import ru.yandex.practicum.mybankfront.client.TransferApiClient;
import ru.yandex.practicum.mybankfront.client.dto.AccountResponse;
import ru.yandex.practicum.mybankfront.controller.dto.AccountDto;
import ru.yandex.practicum.mybankfront.controller.dto.CashAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public void loadAccount(Model model, String login) {
        fillModel(model, login, null, null);
    }

    public void updateAccount(Model model, String login, String name, LocalDate birthdate) {
        accountApiClient.update(login, name, birthdate);
        fillModel(model, login, null, "Данные обновлены");
    }

    public void processCash(Model model, String login, int value, CashAction action) {
        BigDecimal amount = BigDecimal.valueOf(value);
        if (action == CashAction.PUT) {
            cashApiClient.deposit(amount);
            fillModel(model, login, null, "Положено %d руб".formatted(value));
        } else {
            cashApiClient.withdraw(amount);
            fillModel(model, login, null, "Снято %d руб".formatted(value));
        }
    }

    public void processTransfer(Model model, String login, String targetLogin, int value) {
        transferApiClient.transfer(targetLogin, BigDecimal.valueOf(value));
        fillModel(model, login, null, "Успешно переведено %d руб".formatted(value));
    }

    public void fillModel(Model model, String login, List<String> errors, String info) {
        AccountResponse account = accountApiClient.getByLogin(login);
        List<AccountResponse> allAccounts = accountApiClient.getAll();

        model.addAttribute("name", account.name());
        model.addAttribute("birthdate", account.birthdate().format(DateTimeFormatter.ISO_DATE));
        model.addAttribute("sum", account.balance().intValue());
        model.addAttribute("accounts", allAccounts.stream()
                .filter(a -> !a.login().equals(login))
                .map(a -> new AccountDto(a.login(), a.name()))
                .toList());
        model.addAttribute("errors", errors);
        model.addAttribute("info", info);
    }
}
