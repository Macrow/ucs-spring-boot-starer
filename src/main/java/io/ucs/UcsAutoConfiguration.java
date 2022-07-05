package io.ucs;

import io.ucs.config.UcsConfig;
import io.ucs.core.*;
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
@Import({UcsAuthAspectHandler.class, UcsClientAuthAspectHandler.class, UcsPermByActionAspectHandler.class, UcsPermByCodeAspectHandler.class})
@RequiredArgsConstructor
public class UcsAutoConfiguration {
    final UcsConfig ucsConfig;

    @Bean
    public UcsHttpClient httpUcsClient() {
        return new UcsHttpClient(
                ucsConfig.getServiceBaseUrl(),
                ucsConfig.getClientAccessCode(),
                ucsConfig.getClientId(),
                ucsConfig.getClientSecret(),
                ucsConfig.getAccessCodeHeader(),
                ucsConfig.getRandomKeyHeader(),
                ucsConfig.getUserTokenHeader(),
                ucsConfig.getUserTokenHeaderSchema(),
                ucsConfig.getClientTokenHeader(),
                ucsConfig.getClientTokenHeaderSchema()
        );
    }

    @Bean
    public UcsMetaInfoExtractor ucsMetaInfoExtractor() {
        return new UcsMetaInfoExtractor(ucsConfig);
    }
}
