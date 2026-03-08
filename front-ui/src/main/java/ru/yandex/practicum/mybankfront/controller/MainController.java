package ru.yandex.practicum.mybankfront.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.mybankfront.controller.dto.CashAction;
import ru.yandex.practicum.mybankfront.service.BankService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MainController {

    private final BankService bankService;

    public MainController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public String index() {
        return "redirect:/account";
    }

    @GetMapping("/account")
    public String getAccount(Model model, OAuth2AuthenticationToken auth) {
        try {
            bankService.loadAccount(model, getLogin(auth));
        } catch (Exception e) {
            model.addAttribute("errors", List.of(e.getMessage()));
        }
        return "main";
    }

    @PostMapping("/account")
    public String editAccount(Model model, OAuth2AuthenticationToken auth,
                              @RequestParam("name") String name,
                              @RequestParam("birthdate") LocalDate birthdate) {
        String login = getLogin(auth);
        try {
            bankService.updateAccount(model, login, name, birthdate);
        } catch (Exception e) {
            safeLoad(model, login);
            model.addAttribute("errors", List.of(e.getMessage()));
        }
        return "main";
    }

    @PostMapping("/cash")
    public String editCash(Model model, OAuth2AuthenticationToken auth,
                           @RequestParam("value") int value,
                           @RequestParam("action") CashAction action) {
        String login = getLogin(auth);
        try {
            bankService.processCash(model, login, value, action);
        } catch (Exception e) {
            safeLoad(model, login);
            model.addAttribute("errors", List.of(e.getMessage()));
        }
        return "main";
    }

    @PostMapping("/transfer")
    public String transfer(Model model, OAuth2AuthenticationToken auth,
                           @RequestParam("value") int value,
                           @RequestParam("login") String targetLogin) {
        String login = getLogin(auth);
        try {
            bankService.processTransfer(model, login, targetLogin, value);
        } catch (Exception e) {
            safeLoad(model, login);
            model.addAttribute("errors", List.of(e.getMessage()));
        }
        return "main";
    }

    private String getLogin(OAuth2AuthenticationToken auth) {
        return auth.getPrincipal().getAttribute("preferred_username");
    }

    private void safeLoad(Model model, String login) {
        try {
            bankService.loadAccount(model, login);
        } catch (Exception ignored) {
        }
    }
}
