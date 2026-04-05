package co.kremnev.transfer.controller;

import co.kremnev.starter.NotificationClient;
import co.kremnev.transfer.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.transfer-service-client.client-id=test",
        "spring.security.oauth2.client.registration.transfer-service-client.client-secret=test",
        "spring.security.oauth2.client.registration.transfer-service-client.authorization-grant-type=client_credentials",
        "spring.security.oauth2.client.provider.transfer-service-client.token-uri=http://localhost:0/token"
})
@AutoConfigureMockMvc
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private RestClient.Builder restClientBuilder;

    @MockitoBean
    private NotificationClient notificationClient;

    @Test
    void transfer_withValidJwt_returns200() throws Exception {
        mockMvc.perform(post("/transfers")
                        .with(jwt().jwt(j -> j.claim("preferred_username", "ivanov"))
                                .authorities(() -> "SCOPE_transfer"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"toLogin": "petrov", "amount": 300}
                                """))
                .andExpect(status().isOk());

        verify(transferService).transfer("ivanov", "petrov", BigDecimal.valueOf(300));
    }

    @Test
    void transfer_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"toLogin": "petrov", "amount": 300}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void transfer_wrongScope_returns403() throws Exception {
        mockMvc.perform(post("/transfers")
                        .with(jwt().jwt(j -> j.claim("preferred_username", "ivanov"))
                                .authorities(() -> "SCOPE_wrong"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"toLogin": "petrov", "amount": 300}
                                """))
                .andExpect(status().isForbidden());
    }
}
