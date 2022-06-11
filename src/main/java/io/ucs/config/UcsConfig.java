package io.ucs.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Macrow
 * @date 2022/06/11
 */
@Setter
@Getter
@ConfigurationProperties(prefix = UcsConfig.PREFIX)
public class UcsConfig {
    public static final String PREFIX = "ucs";

    private Boolean enable;
    private String appServiceName;
    private String serviceBaseUrl;
    private String accessCode;
    private String clientId;
    private String clientSecret;
    private String accessCodeHeader = "Access-Code";
    private String randomKeyHeader = "Random-Key";
    private String userTokenHeader = "Authorization";
    private String clientTokenHeader = "Client-Authorization";
}
