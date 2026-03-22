package co.kremnev.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication
public class ServiceErrorHandlerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ErrorHandler.class)
    public ErrorHandler myBankErrorHandler() {
        return new ErrorHandler();
    }
}
