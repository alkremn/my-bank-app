package co.kremnev.starter;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
public class KafkaNotificationProducerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KafkaNotificationProducer kafkaNotificationProducer(
            KafkaTemplate<String, ?> kafkaTemplate,
            @Value("${my-bank.notification.topic:notifications}") String topic,
            ObjectProvider<MeterRegistry> meterRegistryProvider) {
        return new KafkaNotificationProducer(kafkaTemplate, topic, meterRegistryProvider.getIfAvailable());
    }
}
