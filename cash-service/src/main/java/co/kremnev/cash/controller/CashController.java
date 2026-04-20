package co.kremnev.cash.controller;

import co.kremnev.cash.controller.dto.CashRequestDto;
import co.kremnev.cash.service.CashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/deposit")
    public void deposit(@RequestBody CashRequestDto request, JwtAuthenticationToken auth) {
        String login = getLogin(auth);
        log.info("Deposit request: login={}, amount={}", login, request.getAmount());
        cashService.deposit(login, request.getAmount());
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody CashRequestDto request, JwtAuthenticationToken auth) {
        String login = getLogin(auth);
        log.info("Withdraw request: login={}, amount={}", login, request.getAmount());
        cashService.withdraw(login, request.getAmount());
    }

    private String getLogin(JwtAuthenticationToken auth) {
        return auth.getToken().getClaimAsString("preferred_username");
    }
}
