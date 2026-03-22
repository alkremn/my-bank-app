package co.kremnev.accounts.contract;

import co.kremnev.accounts.model.Account;
import co.kremnev.accounts.repository.AccountRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public abstract class ContractTestBase {

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
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.postProcessors(
                SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(() -> "SCOPE_accounts")
        );

        accountRepository.deleteAll();
        accountRepository.save(new Account(null, "ivanov", "Ivan Ivanov",
                LocalDate.of(1990, 1, 15), BigDecimal.valueOf(1000)));
        accountRepository.save(new Account(null, "petrov", "Petr Petrov",
                LocalDate.of(1985, 5, 10), BigDecimal.valueOf(500)));
    }
}
