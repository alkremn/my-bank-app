package co.kremnev.accounts.service;

import co.kremnev.accounts.controller.dto.AccountDto;
import co.kremnev.accounts.model.Account;
import co.kremnev.accounts.model.OutboxEvent;
import co.kremnev.accounts.repository.AccountRepository;
import co.kremnev.accounts.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final OutboxRepository outboxRepository;

    public Account create(AccountDto dto) {
        log.debug("Creating account: login={}", dto.getLogin());
        var account = new Account(null, dto.getLogin(), dto.getName(), dto.getBirthdate(), BigDecimal.ZERO);
        var saved = accountRepository.save(account);
        log.info("Account created: login={}, id={}", saved.getLogin(), saved.getId());
        return saved;
    }

    public Account getByLogin(String login) {
        return accountRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.warn("Account not found: login={}", login);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Аккаунт не найден: " + login);
                });
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account update(String login, AccountDto dto) {
        var account = getByLogin(login);
        account.setName(dto.getName());
        account.setBirthdate(dto.getBirthdate());
        log.debug("Updating account: login={}", login);
        return accountRepository.save(account);
    }

    @Transactional
    public void transfer(String fromLogin, String toLogin, BigDecimal amount) {
        log.debug("Executing transfer: from={} to={} amount={}", fromLogin, toLogin, amount);
        updateBalance(fromLogin, amount.negate());
        updateBalance(toLogin, amount);
        log.info("Transfer completed: from={} to={} amount={}", fromLogin, toLogin, amount);
    }

    @Transactional
    public Account updateBalance(String login, BigDecimal amount) {
        var account = getByLogin(login);
        var newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Insufficient funds: login={}, balance={}, requested={}", login, account.getBalance(), amount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недостаточно средств на счёте: " + login);
        }
        account.setBalance(newBalance);
        var saved = accountRepository.save(account);
        String action = amount.compareTo(BigDecimal.ZERO) >= 0 ? "пополнение" : "списание";
        outboxRepository.save(new OutboxEvent(null, login,
                "Баланс изменён (%s): %s руб".formatted(action, amount), Instant.now(), false));
        log.debug("Balance updated: login={}, newBalance={}", login, newBalance);
        return saved;
    }
}
