package ru.yandex.practicum.mybankfront.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

@Controller
public class MainController {

    // TODO: replace with actual login from Security context
    private static final String CURRENT_LOGIN = "ivanov";

    private final AccountApiClient accountApiClient;
    private final CashApiClient cashApiClient;
    private final TransferApiClient transferApiClient;

    public MainController(AccountApiClient accountApiClient,
                          CashApiClient cashApiClient,
                          TransferApiClient transferApiClient) {
        this.accountApiClient = accountApiClient;
        this.cashApiClient = cashApiClient;
        this.transferApiClient = transferApiClient;
    }

    @GetMapping
    public String index() {
        return "redirect:/account";
    }

    @GetMapping("/account")
    public String getAccount(Model model) {
        try {
            fillModel(model, null, null);
        } catch (Exception e) {
            model.addAttribute("errors", List.of(e.getMessage()));
        }
        return "main";
    }

    @PostMapping("/account")
    public String editAccount(
            Model model,
            @RequestParam("name") String name,
            @RequestParam("birthdate") LocalDate birthdate
    ) {
        try {
            accountApiClient.update(CURRENT_LOGIN, name, birthdate);
            fillModel(model, null, "Данные обновлены");
        } catch (Exception e) {
            fillModelSafe(model);
            model.addAttribute("errors", List.of(e.getMessage()));
        }
        return "main";
    }

    @PostMapping("/cash")
    public String editCash(
            Model model,
            @RequestParam("value") int value,
            @RequestParam("action") CashAction action
    ) {
        try {
            BigDecimal amount = BigDecimal.valueOf(value);
            if (action == CashAction.PUT) {
                cashApiClient.deposit(CURRENT_LOGIN, amount);
                fillModel(model, null, "Положено %d руб".formatted(value));
            } else {
                cashApiClient.withdraw(CURRENT_LOGIN, amount);
                fillModel(model, null, "Снято %d руб".formatted(value));
            }
        } catch (Exception e) {
            fillModelSafe(model);
            model.addAttribute("errors", List.of(e.getMessage()));
        }
        return "main";
    }

    @PostMapping("/transfer")
    public String transfer(
            Model model,
            @RequestParam("value") int value,
            @RequestParam("login") String login
    ) {
        try {
            transferApiClient.transfer(CURRENT_LOGIN, login, BigDecimal.valueOf(value));
            fillModel(model, null, "Успешно переведено %d руб".formatted(value));
        } catch (Exception e) {
            fillModelSafe(model);
            model.addAttribute("errors", List.of(e.getMessage()));
        }
        return "main";
    }

    private void fillModel(Model model, List<String> errors, String info) {
        AccountResponse account = accountApiClient.getByLogin(CURRENT_LOGIN);
        List<AccountResponse> allAccounts = accountApiClient.getAll();

        model.addAttribute("name", account.name());
        model.addAttribute("birthdate", account.birthdate().format(DateTimeFormatter.ISO_DATE));
        model.addAttribute("sum", account.balance().intValue());
        model.addAttribute("accounts", allAccounts.stream()
                .filter(a -> !a.login().equals(CURRENT_LOGIN))
                .map(a -> new AccountDto(a.login(), a.name()))
                .toList());
        model.addAttribute("errors", errors);
        model.addAttribute("info", info);
    }

    private void fillModelSafe(Model model) {
        try {
            fillModel(model, null, null);
        } catch (Exception ignored) {
        }
    }
}
