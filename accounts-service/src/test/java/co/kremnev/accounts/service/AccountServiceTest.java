package co.kremnev.accounts.service;

import co.kremnev.accounts.controller.dto.AccountDto;
import co.kremnev.accounts.model.Account;
import co.kremnev.accounts.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private RestClient.Builder restClientBuilder;

    @InjectMocks
    private AccountService accountService;

    @Test
    void create_savesAccountWithZeroBalance() {
        var dto = new AccountDto();
        dto.setLogin("ivanov");
        dto.setName("Ivan Ivanov");
        dto.setBirthdate(LocalDate.of(1990, 1, 15));

        var saved = new Account(1L, "ivanov", "Ivan Ivanov", LocalDate.of(1990, 1, 15), BigDecimal.ZERO);
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        var result = accountService.create(dto);

        assertEquals("ivanov", result.getLogin());
        assertEquals("Ivan Ivanov", result.getName());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void getByLogin_found() {
        var account = new Account(1L, "ivanov", "Ivan Ivanov", LocalDate.of(1990, 1, 15), BigDecimal.valueOf(1000));
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(account));

        var result = accountService.getByLogin("ivanov");

        assertEquals("ivanov", result.getLogin());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
    }

    @Test
    void getByLogin_notFound_throwsException() {
        when(accountRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        var ex = assertThrows(RuntimeException.class, () -> accountService.getByLogin("unknown"));
        assertTrue(ex.getMessage().contains("Аккаунт не найден"));
    }

    @Test
    void getAll_returnsList() {
        var accounts = List.of(
                new Account(1L, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(1000)),
                new Account(2L, "petrov", "Petr", LocalDate.of(1985, 5, 10), BigDecimal.valueOf(500))
        );
        when(accountRepository.findAll()).thenReturn(accounts);

        var result = accountService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void update_changesNameAndBirthdate() {
        var existing = new Account(1L, "ivanov", "Ivan Ivanov", LocalDate.of(1990, 1, 15), BigDecimal.valueOf(1000));
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = new AccountDto();
        dto.setName("Ivan Updated");
        dto.setBirthdate(LocalDate.of(1991, 2, 20));

        var result = accountService.update("ivanov", dto);

        assertEquals("Ivan Updated", result.getName());
        assertEquals(LocalDate.of(1991, 2, 20), result.getBirthdate());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
    }

    @Test
    void updateBalance_addsAmount() {
        var account = new Account(1L, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(1000));
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = accountService.updateBalance("ivanov", BigDecimal.valueOf(500));

        assertEquals(0, BigDecimal.valueOf(1500).compareTo(result.getBalance()));
    }

    @Test
    void updateBalance_insufficientFunds_throwsException() {
        var account = new Account(1L, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(100));
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(account));

        var ex = assertThrows(RuntimeException.class,
                () -> accountService.updateBalance("ivanov", BigDecimal.valueOf(-200)));
        assertTrue(ex.getMessage().contains("Недостаточно средств"));
    }

    @Test
    void transfer_debitsAndCredits() {
        var sender = new Account(1L, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(1000));
        var receiver = new Account(2L, "petrov", "Petr", LocalDate.of(1985, 5, 10), BigDecimal.valueOf(500));

        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(sender));
        when(accountRepository.findByLogin("petrov")).thenReturn(Optional.of(receiver));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.transfer("ivanov", "petrov", BigDecimal.valueOf(300));

        assertEquals(0, BigDecimal.valueOf(700).compareTo(sender.getBalance()));
        assertEquals(0, BigDecimal.valueOf(800).compareTo(receiver.getBalance()));
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transfer_insufficientFunds_throwsException() {
        var sender = new Account(1L, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(100));
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(sender));

        assertThrows(RuntimeException.class,
                () -> accountService.transfer("ivanov", "petrov", BigDecimal.valueOf(500)));
    }
}
