package co.kremnev.cash.controller;

import co.kremnev.cash.controller.dto.CashRequestDto;
import co.kremnev.cash.service.CashService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/deposit")
    public void deposit(@RequestBody CashRequestDto request, JwtAuthenticationToken auth) {
        cashService.deposit(getLogin(auth), request.getAmount());
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody CashRequestDto request, JwtAuthenticationToken auth) {
        cashService.withdraw(getLogin(auth), request.getAmount());
    }

    private String getLogin(JwtAuthenticationToken auth) {
        return auth.getToken().getClaimAsString("preferred_username");
    }
}
