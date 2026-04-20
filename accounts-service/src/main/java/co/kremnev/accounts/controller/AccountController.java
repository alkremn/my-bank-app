package co.kremnev.accounts.controller;

import co.kremnev.accounts.controller.dto.AccountDto;
import co.kremnev.accounts.controller.dto.BalanceUpdateDto;
import co.kremnev.accounts.controller.dto.TransferDto;
import co.kremnev.accounts.model.Account;
import co.kremnev.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public Account create(@RequestBody AccountDto dto) {
        log.info("Creating account for login={}", dto.getLogin());
        return accountService.create(dto);
    }

    @GetMapping("/{login}")
    public Account getByLogin(@PathVariable String login) {
        log.debug("Get account by login={}", login);
        return accountService.getByLogin(login);
    }

    @GetMapping
    public List<Account> getAll() {
        log.debug("Get all accounts");
        return accountService.getAll();
    }

    @PutMapping("/{login}")
    public Account update(@PathVariable String login, @RequestBody AccountDto dto) {
        log.info("Updating account login={}", login);
        return accountService.update(login, dto);
    }

    @PostMapping("/{login}/balance")
    public Account updateBalance(@PathVariable String login, @RequestBody BalanceUpdateDto dto) {
        log.info("Updating balance for login={}, amount={}", login, dto.getAmount());
        return accountService.updateBalance(login, dto.getAmount());
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferDto dto) {
        log.info("Transfer from={} to={} amount={}", dto.getFromLogin(), dto.getToLogin(), dto.getAmount());
        accountService.transfer(dto.getFromLogin(), dto.getToLogin(), dto.getAmount());
    }
}
