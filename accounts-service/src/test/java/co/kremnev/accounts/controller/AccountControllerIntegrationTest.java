package co.kremnev.accounts.controller;

import co.kremnev.accounts.model.Account;
import co.kremnev.accounts.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bankdb")
            .withInitScript("init-schema.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void createAndGetAccount() throws Exception {
        mockMvc.perform(post("/accounts")
                        .with(jwt().authorities(() -> "SCOPE_accounts"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "ivanov", "name": "Ivan Ivanov", "birthdate": "1990-01-15"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("ivanov"))
                .andExpect(jsonPath("$.balance").value(0));

        mockMvc.perform(get("/accounts/ivanov")
                        .with(jwt().authorities(() -> "SCOPE_accounts")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ivan Ivanov"));
    }

    @Test
    void getAll_returnsAllAccounts() throws Exception {
        accountRepository.save(new Account(null, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(1000)));
        accountRepository.save(new Account(null, "petrov", "Petr", LocalDate.of(1985, 5, 10), BigDecimal.valueOf(500)));

        mockMvc.perform(get("/accounts")
                        .with(jwt().authorities(() -> "SCOPE_accounts")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateBalance_addsAmount() throws Exception {
        accountRepository.save(new Account(null, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(1000)));

        mockMvc.perform(post("/accounts/ivanov/balance")
                        .with(jwt().authorities(() -> "SCOPE_accounts"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 500}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void updateBalance_insufficientFunds_returnsBadRequest() throws Exception {
        accountRepository.save(new Account(null, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(100)));

        mockMvc.perform(post("/accounts/ivanov/balance")
                        .with(jwt().authorities(() -> "SCOPE_accounts"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": -200}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Недостаточно средств")));
    }

    @Test
    void transfer_movesBalance() throws Exception {
        accountRepository.save(new Account(null, "ivanov", "Ivan", LocalDate.of(1990, 1, 1), BigDecimal.valueOf(1000)));
        accountRepository.save(new Account(null, "petrov", "Petr", LocalDate.of(1985, 5, 10), BigDecimal.valueOf(500)));

        mockMvc.perform(post("/accounts/transfer")
                        .with(jwt().authorities(() -> "SCOPE_accounts"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromLogin": "ivanov", "toLogin": "petrov", "amount": 300}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/accounts/ivanov")
                        .with(jwt().authorities(() -> "SCOPE_accounts")))
                .andExpect(jsonPath("$.balance").value(700));

        mockMvc.perform(get("/accounts/petrov")
                        .with(jwt().authorities(() -> "SCOPE_accounts")))
                .andExpect(jsonPath("$.balance").value(800));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isUnauthorized());
    }
}
