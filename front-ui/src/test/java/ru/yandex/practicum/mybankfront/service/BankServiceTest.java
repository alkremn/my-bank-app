package ru.yandex.practicum.mybankfront.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.yandex.practicum.mybankfront.client.AccountApiClient;
import ru.yandex.practicum.mybankfront.client.CashApiClient;
import ru.yandex.practicum.mybankfront.client.TransferApiClient;
import ru.yandex.practicum.mybankfront.client.dto.AccountResponse;
import ru.yandex.practicum.mybankfront.controller.dto.CashAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private AccountApiClient accountApiClient;

    @Mock
    private CashApiClient cashApiClient;

    @Mock
    private TransferApiClient transferApiClient;

    @InjectMocks
    private BankService bankService;

    private final AccountResponse ivanov = new AccountResponse(
            1L, "ivanov", "Ivan", LocalDate.of(1990, 1, 15), BigDecimal.valueOf(1000));

    private final AccountResponse petrov = new AccountResponse(
            2L, "petrov", "Petr", LocalDate.of(1985, 5, 10), BigDecimal.valueOf(500));

    @Test
    void loadAccount_populatesModel() {
        when(accountApiClient.getByLogin("ivanov")).thenReturn(ivanov);
        when(accountApiClient.getAll()).thenReturn(List.of(ivanov, petrov));

        Model model = new ConcurrentModel();
        bankService.loadAccount(model, "ivanov");

        assertEquals("Ivan", model.getAttribute("name"));
        assertEquals(1000, model.getAttribute("sum"));
    }

    @Test
    void updateAccount_callsClientAndPopulatesModel() {
        when(accountApiClient.getByLogin("ivanov")).thenReturn(ivanov);
        when(accountApiClient.getAll()).thenReturn(List.of(ivanov));

        Model model = new ConcurrentModel();
        bankService.updateAccount(model, "ivanov", "Ivan Updated", LocalDate.of(1990, 1, 15));

        verify(accountApiClient).update("ivanov", "Ivan Updated", LocalDate.of(1990, 1, 15));
        assertEquals("Данные обновлены", model.getAttribute("info"));
    }

    @Test
    void processCash_deposit_callsDepositAndPopulatesModel() {
        when(accountApiClient.getByLogin("ivanov")).thenReturn(ivanov);
        when(accountApiClient.getAll()).thenReturn(List.of(ivanov));

        Model model = new ConcurrentModel();
        bankService.processCash(model, "ivanov", 500, CashAction.PUT);

        verify(cashApiClient).deposit(BigDecimal.valueOf(500));
        assertEquals("Положено 500 руб", model.getAttribute("info"));
    }

    @Test
    void processCash_withdraw_callsWithdrawAndPopulatesModel() {
        when(accountApiClient.getByLogin("ivanov")).thenReturn(ivanov);
        when(accountApiClient.getAll()).thenReturn(List.of(ivanov));

        Model model = new ConcurrentModel();
        bankService.processCash(model, "ivanov", 200, CashAction.GET);

        verify(cashApiClient).withdraw(BigDecimal.valueOf(200));
        assertEquals("Снято 200 руб", model.getAttribute("info"));
    }

    @Test
    void processTransfer_callsTransferAndPopulatesModel() {
        when(accountApiClient.getByLogin("ivanov")).thenReturn(ivanov);
        when(accountApiClient.getAll()).thenReturn(List.of(ivanov, petrov));

        Model model = new ConcurrentModel();
        bankService.processTransfer(model, "ivanov", "petrov", 300);

        verify(transferApiClient).transfer("petrov", BigDecimal.valueOf(300));
        assertEquals("Успешно переведено 300 руб", model.getAttribute("info"));
    }

    @Test
    void fillModel_filtersCurrentUserFromAccountsList() {
        when(accountApiClient.getByLogin("ivanov")).thenReturn(ivanov);
        when(accountApiClient.getAll()).thenReturn(List.of(ivanov, petrov));

        Model model = new ConcurrentModel();
        bankService.fillModel(model, "ivanov", null, null);

        @SuppressWarnings("unchecked")
        List<?> accounts = (List<?>) model.getAttribute("accounts");
        assertEquals(1, accounts.size());
    }
}
