package io.ucs.sdk;

import io.ucs.sdk.entity.JwtUser;
import io.ucs.sdk.entity.PermitResult;
import io.ucs.sdk.entity.UcsResult;

import java.util.Map;

/**
 * @author Macrow
 * @date 2022-03-17
 */
public interface Client {
    Client setTimeout(int timeout);
    Client setBaseUrl(String baseUrl);
    Client setAccessCode(String accessCode);
    Client setUserToken(String token);
    Client setClientIdAndSecret(String clientId, String clientSecret);
    Client setHttpHeaderNames(String accessCodeHeader, String randomKeyHeader, String userTokenHeader, String clientTokenHeader);

    UcsResult<JwtUser> userValidateJwt();
    UcsResult<PermitResult> userValidatePermByOperation(String code);
    UcsResult<PermitResult> userValidatePermByAction(String service, String method, String path);

    <T> UcsResult<T> userRequest(Class<T> klass, String method, String url, Map<String, Object> data);
    <T> UcsResult<T> clientRequest(Class<T> klass, String method, String url, Map<String, Object> data);
}
