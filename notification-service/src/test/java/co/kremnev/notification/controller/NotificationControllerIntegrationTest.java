package co.kremnev.notification.controller;

import co.kremnev.notification.model.Notification;
import co.kremnev.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bankdb")
            .withInitScript("init-schema.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @Test
    void createNotification() throws Exception {
        mockMvc.perform(post("/notifications")
                        .with(jwt().authorities(() -> "SCOPE_notifications"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "ivanov", "message": "Deposit 500 rub"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("ivanov"))
                .andExpect(jsonPath("$.message").value("Deposit 500 rub"));
    }

    @Test
    void getByLogin_returnsNotifications() throws Exception {
        notificationRepository.save(new Notification(null, "ivanov", "Deposit 500", LocalDateTime.now()));
        notificationRepository.save(new Notification(null, "ivanov", "Withdraw 200", LocalDateTime.now()));

        mockMvc.perform(get("/notifications/ivanov")
                        .with(jwt().authorities(() -> "SCOPE_notifications")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getByLogin_noNotifications_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/notifications/unknown")
                        .with(jwt().authorities(() -> "SCOPE_notifications")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/notifications/ivanov"))
                .andExpect(status().isUnauthorized());
    }
}
