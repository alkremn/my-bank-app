package co.kremnev.cash.controller;

import co.kremnev.cash.controller.dto.CashRequestDto;
import co.kremnev.cash.service.CashService;
import lombok.RequiredArgsConstructor;
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
    public void deposit(@RequestBody CashRequestDto request) {
        cashService.deposit(request.getLogin(), request.getAmount());
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody CashRequestDto request) {
        cashService.withdraw(request.getLogin(), request.getAmount());
    }
}
