package io.ucs;

import io.ucs.config.UcsConfig;
import io.ucs.core.UcsAuthAspectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Macrow
 * @date 2022/06/11
 */
@Configuration
@Import({UcsAuthAspectHandler.class})
public class UcsConfiguration {
    @Bean
    public UcsConfig ucsConfig(){
        return new UcsConfig();
    }
}
