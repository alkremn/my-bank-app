package co.kremnev.starter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(MeterRegistry.class)
public class ObservabilityAutoConfiguration {

    @Bean
    public MeterFilter commonTagsMeterFilter(
            @Value("${spring.application.name:unknown}") String appName) {
        return MeterFilter.commonTags(io.micrometer.core.instrument.Tags.of("application", appName));
    }
}
