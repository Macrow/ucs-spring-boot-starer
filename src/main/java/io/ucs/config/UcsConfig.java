package io.ucs.config;

import io.ucs.sdk.Constant;
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
    private String clientAccessCode;
    private String clientId;
    private String clientSecret;
    private String accessCodeHeader = Constant.DEFAULT_ACCESS_CODE;
    private String randomKeyHeader = Constant.DEFAULT_HEADER_RANDOM_KEY;
    private String userTokenHeader = Constant.DEFAULT_USER_HEADER_NAME;
    private String userTokenHeaderSchema = Constant.DEFAULT_BEARER_TYPE;
    private String clientTokenHeader = Constant.DEFAULT_CLIENT_HEADER_NAME;
    private String clientTokenHeaderSchema = Constant.DEFAULT_BEARER_TYPE;
}
