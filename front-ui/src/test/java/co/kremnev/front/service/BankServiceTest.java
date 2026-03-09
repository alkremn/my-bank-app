package co.kremnev.front.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import co.kremnev.front.client.AccountApiClient;
import co.kremnev.front.client.CashApiClient;
import co.kremnev.front.client.TransferApiClient;
import co.kremnev.front.client.dto.AccountResponse;
import co.kremnev.front.controller.dto.CashAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void getAccount_returnsAccount() {
        when(accountApiClient.getByLogin("ivanov")).thenReturn(ivanov);

        var result = bankService.getAccount("ivanov");

        assertEquals("Ivan", result.name());
        assertEquals(BigDecimal.valueOf(1000), result.balance());
    }

    @Test
    void getOtherAccounts_filtersCurrentUser() {
        when(accountApiClient.getAll()).thenReturn(List.of(ivanov, petrov));

        var result = bankService.getOtherAccounts("ivanov");

        assertEquals(1, result.size());
        assertEquals("petrov", result.get(0).login());
    }

    @Test
    void updateAccount_callsClient() {
        bankService.updateAccount("ivanov", "Ivan Updated", LocalDate.of(1990, 1, 15));

        verify(accountApiClient).update("ivanov", "Ivan Updated", LocalDate.of(1990, 1, 15));
    }

    @Test
    void updateAccount_underage_throwsException() {
        LocalDate underage = LocalDate.now().minusYears(17);

        assertThrows(RuntimeException.class,
                () -> bankService.updateAccount("ivanov", "Ivan", underage));
        verify(accountApiClient, never()).update(any(), any(), any());
    }

    @Test
    void processCash_deposit_callsDeposit() {
        bankService.processCash("ivanov", 500, CashAction.PUT);

        verify(cashApiClient).deposit(BigDecimal.valueOf(500));
    }

    @Test
    void processCash_withdraw_callsWithdraw() {
        bankService.processCash("ivanov", 200, CashAction.GET);

        verify(cashApiClient).withdraw(BigDecimal.valueOf(200));
    }

    @Test
    void processTransfer_callsTransfer() {
        bankService.processTransfer("ivanov", "petrov", 300);

        verify(transferApiClient).transfer("petrov", BigDecimal.valueOf(300));
    }
}
