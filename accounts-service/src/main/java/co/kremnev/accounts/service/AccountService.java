package co.kremnev.accounts.service;

import co.kremnev.accounts.controller.dto.AccountDto;
import co.kremnev.accounts.model.Account;
import co.kremnev.accounts.repository.AccountRepository;
import co.kremnev.starter.KafkaNotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final KafkaNotificationProducer notificationProducer;

    public Account create(AccountDto dto) {
        var account = new Account(null, dto.getLogin(), dto.getName(), dto.getBirthdate(), BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    public Account getByLogin(String login) {
        return accountRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Аккаунт не найден: " + login));
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account update(String login, AccountDto dto) {
        var account = getByLogin(login);
        account.setName(dto.getName());
        account.setBirthdate(dto.getBirthdate());
        return accountRepository.save(account);
    }

    @Transactional
    public void transfer(String fromLogin, String toLogin, BigDecimal amount) {
        updateBalance(fromLogin, amount.negate());
        updateBalance(toLogin, amount);
    }

    public Account updateBalance(String login, BigDecimal amount) {
        var account = getByLogin(login);
        var newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недостаточно средств на счёте: " + login);
        }
        account.setBalance(newBalance);
        var saved = accountRepository.save(account);
        String action = amount.compareTo(BigDecimal.ZERO) >= 0 ? "пополнение" : "списание";
        notificationProducer.send(login, "Баланс изменён (%s): %s руб".formatted(action, amount));
        return saved;
    }
}
