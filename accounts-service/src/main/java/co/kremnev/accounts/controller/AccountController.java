package co.kremnev.accounts.controller;

import co.kremnev.accounts.controller.dto.AccountDto;
import co.kremnev.accounts.controller.dto.BalanceUpdateDto;
import co.kremnev.accounts.model.Account;
import co.kremnev.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public Account create(@RequestBody AccountDto dto) {
        return accountService.create(dto);
    }

    @GetMapping("/{login}")
    public Account getByLogin(@PathVariable String login) {
        return accountService.getByLogin(login);
    }

    @GetMapping
    public List<Account> getAll() {
        return accountService.getAll();
    }

    @PutMapping("/{login}")
    public Account update(@PathVariable String login, @RequestBody AccountDto dto) {
        return accountService.update(login, dto);
    }

    @PostMapping("/{login}/balance")
    public Account updateBalance(@PathVariable String login, @RequestBody BalanceUpdateDto dto) {
        return accountService.updateBalance(login, dto.getAmount());
    }
}
