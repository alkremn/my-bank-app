package co.kremnev.transfer.service;

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

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(restClientBuilder);
    }

    @Test
    void transfer_callsThreeEndpoints() {
        // 1 accounts/transfer + 2 notifications = 3
        transferService.transfer("ivanov", "petrov", BigDecimal.valueOf(300));

        verify(restClientBuilder, times(3)).build();
    }
}
