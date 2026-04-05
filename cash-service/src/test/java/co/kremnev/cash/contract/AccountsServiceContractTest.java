package co.kremnev.cash.contract;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.web.client.RestClient;

import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:1234/jwks",
        "spring.security.oauth2.client.provider.cash-service-client.token-uri=http://localhost:1234/token",
        "spring.security.oauth2.client.registration.cash-service-client.client-id=test",
        "spring.security.oauth2.client.registration.cash-service-client.client-secret=test",
        "spring.security.oauth2.client.registration.cash-service-client.authorization-grant-type=client_credentials",
        "stubrunner.cloud.enabled=false"
})
@AutoConfigureStubRunner(
        ids = "co.kremnev:accounts-service:+:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class AccountsServiceContractTest {

    @StubRunnerPort("accounts-service")
    private int stubPort;

    @Test
    void shouldGetAccountByLogin() {
        RestClient restClient = RestClient.create("http://localhost:" + stubPort);

        Map<?, ?> response = restClient.get()
                .uri("/accounts/ivanov")
                .retrieve()
                .body(Map.class);

        assertThat(response).isNotNull();
        assertThat(response.get("login")).isEqualTo("ivanov");
        assertThat(response.get("name")).isEqualTo("Ivan Ivanov");
        assertThat(((Number) response.get("balance")).intValue()).isEqualTo(1000);
    }

    @Test
    void shouldUpdateBalance() {
        RestClient restClient = RestClient.create("http://localhost:" + stubPort);

        Map<?, ?> response = restClient.post()
                .uri("/accounts/ivanov/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"amount\": 500}")
                .retrieve()
                .body(Map.class);

        assertThat(response).isNotNull();
        assertThat(response.get("login")).isEqualTo("ivanov");
        assertThat(((Number) response.get("balance")).intValue()).isEqualTo(1500);
    }
}
