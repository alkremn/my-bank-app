package co.kremnev.transfer.service;

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
class TransferServiceTest {

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private RestClient.Builder restClientBuilder;

    @Mock
    private KafkaNotificationProducer notificationProducer;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(restClientBuilder, notificationProducer);
    }

    @Test
    void transfer_callsAccountsAndNotifications() {
        transferService.transfer("ivanov", "petrov", BigDecimal.valueOf(300));

        verify(restClientBuilder).build();
        verify(notificationProducer).send(eq("ivanov"), anyString());
        verify(notificationProducer).send(eq("petrov"), anyString());
    }
}
