package co.kremnev.cash.controller;

import co.kremnev.cash.service.CashService;
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
        "spring.security.oauth2.client.registration.cash-service-client.client-id=test",
        "spring.security.oauth2.client.registration.cash-service-client.client-secret=test",
        "spring.security.oauth2.client.registration.cash-service-client.authorization-grant-type=client_credentials",
        "spring.security.oauth2.client.provider.cash-service-client.token-uri=http://localhost:0/token"
})
@AutoConfigureMockMvc
class CashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CashService cashService;

    @MockitoBean
    private RestClient.Builder restClientBuilder;

    @Test
    void deposit_withValidJwt_returns200() throws Exception {
        mockMvc.perform(post("/cash/deposit")
                        .with(jwt().jwt(j -> j.claim("preferred_username", "ivanov"))
                                .authorities(() -> "SCOPE_cash"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 500}
                                """))
                .andExpect(status().isOk());

        verify(cashService).deposit("ivanov", BigDecimal.valueOf(500));
    }

    @Test
    void withdraw_withValidJwt_returns200() throws Exception {
        mockMvc.perform(post("/cash/withdraw")
                        .with(jwt().jwt(j -> j.claim("preferred_username", "ivanov"))
                                .authorities(() -> "SCOPE_cash"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 200}
                                """))
                .andExpect(status().isOk());

        verify(cashService).withdraw("ivanov", BigDecimal.valueOf(200));
    }

    @Test
    void deposit_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/cash/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 500}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deposit_wrongScope_returns403() throws Exception {
        mockMvc.perform(post("/cash/deposit")
                        .with(jwt().jwt(j -> j.claim("preferred_username", "ivanov"))
                                .authorities(() -> "SCOPE_wrong"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 500}
                                """))
                .andExpect(status().isForbidden());
    }
}
