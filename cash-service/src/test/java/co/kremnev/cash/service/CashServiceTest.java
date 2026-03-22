package co.kremnev.cash.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private RestClient.Builder restClientBuilder;

    private CashService cashService;

    @BeforeEach
    void setUp() {
        cashService = new CashService(restClientBuilder);
    }

    @Test
    void deposit_callsTwoEndpoints() {
        cashService.deposit("ivanov", BigDecimal.valueOf(500));

        verify(restClientBuilder, times(2)).build();
    }

    @Test
    void withdraw_callsTwoEndpoints() {
        cashService.withdraw("ivanov", BigDecimal.valueOf(200));

        verify(restClientBuilder, times(2)).build();
    }
}
