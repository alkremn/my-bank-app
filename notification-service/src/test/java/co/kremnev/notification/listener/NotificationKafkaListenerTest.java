package co.kremnev.notification.listener;

import co.kremnev.notification.repository.NotificationRepository;
import co.kremnev.starter.NotificationEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = "notifications")
class NotificationKafkaListenerTest {

    @TestConfiguration
    static class KafkaTestConfig {
        @Bean
        public KafkaTemplate<String, NotificationEvent> kafkaTemplate(EmbeddedKafkaBroker broker) {
            var props = Map.<String, Object>of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString(),
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
            );
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
        }
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bankdb")
            .withInitScript("init-schema.sql");

    @Autowired
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @Test
    void shouldConsumeNotificationEvent() {
        kafkaTemplate.send("notifications", "ivanov", new NotificationEvent("ivanov", "Deposit: +500"));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var notifications = notificationRepository.findByLoginOrderByCreatedAtDesc("ivanov");
            assertThat(notifications).hasSize(1);
            assertThat(notifications.get(0).getLogin()).isEqualTo("ivanov");
            assertThat(notifications.get(0).getMessage()).isEqualTo("Deposit: +500");
        });
    }
}
