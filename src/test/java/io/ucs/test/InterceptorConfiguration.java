package io.ucs.test;

import io.ucs.config.UcsConfig;
import io.ucs.core.UcsMetaInfoExtractor;
import io.ucs.sdk.UcsHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Macrow
 * @date 2022/08/01
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorConfiguration implements WebMvcConfigurer {
    final UcsMetaInfoExtractor ucsMetaInfoExtractor;
    final UcsHttpClient ucsHttpClient;
    final UcsConfig ucsConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UcsInterceptor(ucsMetaInfoExtractor, ucsHttpClient, ucsConfig))
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }
}
