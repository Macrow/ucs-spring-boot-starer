package io.ucs;

import io.ucs.config.UcsConfig;
import io.ucs.core.UcsAuthAspectHandler;
import io.ucs.core.UcsPermByActionAspectHandler;
import io.ucs.core.UcsPermByCodeAspectHandler;
import io.ucs.sdk.UcsHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Macrow
 * @date 2022/06/11
 */
@Configuration
@ConditionalOnProperty(prefix = UcsConfig.PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(UcsConfig.class)
@Import({UcsAuthAspectHandler.class, UcsPermByActionAspectHandler.class, UcsPermByCodeAspectHandler.class})
@RequiredArgsConstructor
public class UcsAutoConfiguration {
    final UcsConfig ucsConfig;

    @Bean
    public UcsHttpClient httpUcsClient() {
        return new UcsHttpClient(ucsConfig.getServiceBaseUrl(), ucsConfig.getAccessCode(), ucsConfig.getClientId(), ucsConfig.getClientSecret());
    }
}
