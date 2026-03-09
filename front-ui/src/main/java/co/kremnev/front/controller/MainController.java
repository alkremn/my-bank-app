package co.kremnev.front.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import co.kremnev.front.client.dto.AccountResponse;
import co.kremnev.front.controller.dto.AccountDto;
import co.kremnev.front.controller.dto.CashAction;
import co.kremnev.front.service.BankService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        String login = getLogin(auth);
        try {
            AccountResponse account = bankService.getAccount(login);
            model.addAttribute("name", account.name());
            model.addAttribute("birthdate", account.birthdate().format(DateTimeFormatter.ISO_DATE));
            model.addAttribute("sum", account.balance().intValue());
            model.addAttribute("accounts", bankService.getOtherAccounts(login).stream()
                    .map(a -> new AccountDto(a.login(), a.name()))
                    .toList());
        } catch (Exception e) {
            model.addAttribute("errors", List.of(extractErrorMessage(e)));
        }
        return "main";
    }

    @PostMapping("/account")
    public String editAccount(RedirectAttributes redirectAttributes, OAuth2AuthenticationToken auth,
                              @RequestParam("name") String name,
                              @RequestParam("birthdate") LocalDate birthdate) {
        String login = getLogin(auth);
        try {
            bankService.updateAccount(login, name, birthdate);
            redirectAttributes.addFlashAttribute("info", "Данные обновлены");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of(extractErrorMessage(e)));
        }
        return "redirect:/account";
    }

    @PostMapping("/cash")
    public String editCash(RedirectAttributes redirectAttributes, OAuth2AuthenticationToken auth,
                           @RequestParam("value") int value,
                           @RequestParam("action") CashAction action) {
        try {
            bankService.processCash(getLogin(auth), value, action);
            String msg = action == CashAction.PUT
                    ? "Положено %d руб".formatted(value)
                    : "Снято %d руб".formatted(value);
            redirectAttributes.addFlashAttribute("info", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of(extractErrorMessage(e)));
        }
        return "redirect:/account";
    }

    @PostMapping("/transfer")
    public String transfer(RedirectAttributes redirectAttributes, OAuth2AuthenticationToken auth,
                           @RequestParam("value") int value,
                           @RequestParam("login") String targetLogin) {
        try {
            bankService.processTransfer(getLogin(auth), targetLogin, value);
            redirectAttributes.addFlashAttribute("info", "Успешно переведено %d руб".formatted(value));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errors", List.of(extractErrorMessage(e)));
        }
        return "redirect:/account";
    }

    private String getLogin(OAuth2AuthenticationToken auth) {
        return auth.getPrincipal().getAttribute("preferred_username");
    }

    private String extractErrorMessage(Exception e) {
        if (e instanceof HttpClientErrorException ex) {
            try {
                var body = new ObjectMapper().readTree(ex.getResponseBodyAsString());
                String error = body.path("error").asText(null);
                if (error != null && !error.isEmpty()) {
                    return error;
                }
            } catch (Exception ignored) {
            }
        }
        return e.getMessage();
    }
}
