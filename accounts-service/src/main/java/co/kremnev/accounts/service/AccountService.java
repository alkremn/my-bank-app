package co.kremnev.accounts.service;

import co.kremnev.accounts.controller.dto.AccountDto;
import co.kremnev.accounts.model.Account;
import co.kremnev.accounts.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account create(AccountDto dto) {
        var account = new Account(null, dto.getLogin(), dto.getName(), dto.getBirthdate(), BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    public Account getByLogin(String login) {
        return accountRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Account not found: " + login));
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

    public Account updateBalance(String login, BigDecimal amount) {
        var account = getByLogin(login);
        var newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient funds for account: " + login);
        }
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
}
