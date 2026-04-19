package co.kremnev.cash.service;

import co.kremnev.starter.KafkaNotificationProducer;
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

    @Mock
    private KafkaNotificationProducer notificationProducer;

    private CashService cashService;

    @BeforeEach
    void setUp() {
        cashService = new CashService(restClientBuilder, notificationProducer);
    }

    @Test
    void deposit_callsAccountsAndNotification() {
        cashService.deposit("ivanov", BigDecimal.valueOf(500));

        verify(restClientBuilder).build();
        verify(notificationProducer).send(eq("ivanov"), anyString());
    }

    @Test
    void withdraw_callsAccountsAndNotification() {
        cashService.withdraw("ivanov", BigDecimal.valueOf(200));

        verify(restClientBuilder).build();
        verify(notificationProducer).send(eq("ivanov"), anyString());
    }
}
