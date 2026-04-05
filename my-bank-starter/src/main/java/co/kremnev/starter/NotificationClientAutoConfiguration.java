package co.kremnev.starter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@AutoConfiguration(after = ServiceRestClientAutoConfiguration.class)
@ConditionalOnBean(RestClient.Builder.class)
public class NotificationClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NotificationClient notificationClient(
            RestClient.Builder restClientBuilder,
            @Value("${my-bank.notification.url:http://notification-service}") String notificationUrl) {
        return new NotificationClient(restClientBuilder, notificationUrl);
    }
}
