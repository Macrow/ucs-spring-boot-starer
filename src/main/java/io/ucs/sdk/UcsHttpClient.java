package io.ucs.sdk;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.ucs.sdk.entity.JwtUser;
import io.ucs.sdk.entity.PermitResult;
import io.ucs.sdk.entity.UcsResult;
import kong.unirest.*;
import kong.unirest.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Macrow
 * @date 2022-03-17
 */
public class UcsHttpClient implements Client {
    private String baseUrl;
    private String clientAccessCode;
    private String accessCode;
    private String randomKey;
    private String accessCodeHeader;
    private String randomKeyHeader;
    private String userTokenHeader;
    private String userTokenHeaderSchema;
    private String clientTokenHeader;
    private String clientTokenHeaderSchema;
    private String userToken = null;
    private String clientToken = null;
    private String clientId = null;
    private String clientSecret = null;
    private int timeout = Constant.DEFAULT_TIMEOUT_IN_SECONDS;

    public UcsHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.accessCodeHeader = Constant.DEFAULT_ACCESS_CODE;
        this.randomKeyHeader = Constant.DEFAULT_HEADER_RANDOM_KEY;
        this.userTokenHeader = Constant.DEFAULT_USER_HEADER_NAME;
        this.userTokenHeaderSchema = Constant.DEFAULT_BEARER_TYPE;
        this.clientTokenHeader = Constant.DEFAULT_CLIENT_HEADER_NAME;
        this.clientTokenHeaderSchema = Constant.DEFAULT_BEARER_TYPE;
    }

    public UcsHttpClient(
            String baseUrl,
            String clientAccessCode,
            String clientId,
            String clientSecret,
            String accessCodeHeader,
            String randomKeyHeader,
            String userTokenHeader,
            String userTokenHeaderSchema,
            String clientTokenHeader,
            String clientTokenHeaderSchema
    ) {
        this(baseUrl);
        this.clientAccessCode = clientAccessCode;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessCodeHeader = StrUtil.isEmpty(accessCodeHeader) ? Constant.DEFAULT_ACCESS_CODE : accessCodeHeader;
        this.randomKeyHeader = StrUtil.isEmpty(randomKeyHeader) ? Constant.DEFAULT_HEADER_RANDOM_KEY : randomKeyHeader;
        this.userTokenHeader = StrUtil.isEmpty(userTokenHeader) ? Constant.DEFAULT_USER_HEADER_NAME : userTokenHeader;
        this.userTokenHeaderSchema = StrUtil.isEmpty(userTokenHeaderSchema) ? Constant.DEFAULT_BEARER_TYPE : userTokenHeaderSchema;
        this.clientTokenHeader = StrUtil.isEmpty(clientTokenHeader) ? Constant.DEFAULT_CLIENT_HEADER_NAME : clientTokenHeader;
        this.clientTokenHeaderSchema = StrUtil.isEmpty(clientTokenHeaderSchema) ? Constant.DEFAULT_BEARER_TYPE : clientTokenHeaderSchema;
    }

    @Override
    public Client setTimeout(int timeout) {
        if (timeout > 0) {
            this.timeout = timeout;
        }
        return this;
    }

    @Override
    public Client setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public Client setAccessCode(String accessCode) {
        this.accessCode = accessCode;
        return this;
    }

    @Override
    public Client setRandomKey(String randomKey) {
        this.randomKey = randomKey;
        return this;
    }

    @Override
    public Client setUserToken(String token) {
        this.userToken = token;
        return this;
    }

    @Override
    public Client setClientToken(String token) {
        this.clientToken = token;
        return this;
    }

    @Override
    public Client setClientIdAndSecret(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    public Client setHttpHeaderNames(String accessCodeHeader, String randomKeyHeader, String userTokenHeader, String clientTokenHeader) {
        this.accessCodeHeader = accessCodeHeader;
        this.randomKeyHeader = randomKeyHeader;
        this.userTokenHeader = userTokenHeader;
        this.clientTokenHeader = clientTokenHeader;
        return this;
    }

    @Override
    public UcsResult<JwtUser> userValidateJwt() {
        return request(JwtUser.class, null, "GET", Constant.ValidateJwtURL, null, RequestType.USER, null);
    }

    @Override
    public UcsResult<Void> clientValidate(ClientAuthType clientAuthType) {
        return request(Void.class, null, "GET", Constant.ValidateClientURL, null, RequestType.CLIENT, clientAuthType);
    }

    @Override
    public UcsResult<PermitResult> userValidatePermByOperation(String code, Boolean fulfillJwt, Boolean fulfillOrgIds) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("code", code);
        formData.put("fulfillJwt", fulfillJwt ? "1" : "0");
        formData.put("fulfillOrgIds", fulfillOrgIds ? "1" : "0");
        return request(PermitResult.class, null, "POST", Constant.ValidatePermOperationByCodeURL, formData, RequestType.USER, null);
    }

    @Override
    public UcsResult<PermitResult> userValidatePermByAction(String service, String method, String path, Boolean fulfillJwt, Boolean fulfillOrgIds) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("service", service);
        formData.put("method", method);
        formData.put("path", path);
        formData.put("fulfillJwt", fulfillJwt ? "1" : "0");
        formData.put("fulfillOrgIds", fulfillOrgIds ? "1" : "0");
        return request(PermitResult.class, null, "POST", Constant.ValidatePermActionURL, formData, RequestType.USER, null);
    }

    @Override
    public <T> UcsResult<T> userRequest(Class<T> klass, String method, String url, Map<String, Object> data) {
        return request(klass, null, method, url, data, RequestType.USER, null);
    }

    @Override
    public <T> UcsResult<T> userRequest(Type targetType, String method, String url, Map<String, Object> data) {
        return request(null, targetType, method, url, data, RequestType.USER, null);
    }

    @Override
    public <T> UcsResult<T> clientRequest(Class<T> klass, String method, String url, Map<String, Object> data, ClientAuthType clientAuthType) {
        if (StrUtil.isNotEmpty(this.clientAccessCode)) {
            this.accessCode = this.clientAccessCode;
        }
        this.randomKey = generateRandomKey();
        return request(klass, null, method, url, data, RequestType.CLIENT, clientAuthType);
    }

    @Override
    public <T> UcsResult<T> clientRequest(Type targetType, String method, String url, Map<String, Object> data, ClientAuthType clientAuthType) {
        if (StrUtil.isNotEmpty(this.clientAccessCode)) {
            this.accessCode = this.clientAccessCode;
        }
        this.randomKey = generateRandomKey();
        return request(null, targetType, method, url, data, RequestType.CLIENT, clientAuthType);
    }

    private <T> UcsResult<T> request(Class<T> klass, Type targetType, String method, String url, Map<String, Object> formData, RequestType requestType, ClientAuthType clientAuthType) {
        Map<String, String> headers = new HashMap<>();
        headers.put(this.accessCodeHeader, this.accessCode);
        headers.put(this.randomKeyHeader, this.randomKey);
        switch (requestType) {
            case USER:
                prepareForUserRequest();
                headers.put(this.userTokenHeader, this.userTokenHeaderSchema + " " + this.userToken);
                break;
            case CLIENT:
                headers.put(this.clientTokenHeader, this.clientTokenHeaderSchema + " " + prepareForClientRequest(clientAuthType));
                break;
            default:
                throw new IllegalArgumentException("不支持的请求类型");
        }
        String errMessage;
        HttpResponse<JsonNode> res;
        Unirest.config()
                .reset()
                // 默认关闭SSL安全校验
                .verifySsl(false)
                .connectTimeout(timeout * 1000);
        HttpRequestWithBody req = Unirest
                .request(method, baseUrl + url)
                .contentType(ContentType.APPLICATION_JSON.toString())
                .headers(headers);
        if (formData != null && !formData.isEmpty()) {
            res = req.fields(formData).asJson();
        } else {
            res = req.asJson();
        }
        if (res.getStatus() == HttpStatus.OK) {
            JSONObject body = res.getBody().getObject();
            if (!Objects.equals(body.get("code").toString(), "0")) {
                errMessage = body.get("message").toString();
            } else {
                Object result = body.get("result");
                T t;
                if (klass == Void.class || targetType == Void.TYPE) {
                    t = null;
                } else {
                    if (klass != null) {
                        t = JSONUtil.toBean(result.toString(), klass);
                    } else {
                        t = JSONUtil.toBean(result.toString(), targetType, false);
                    }
                }
                return UcsResult.<T>builder()
                        .success(true)
                        .message("")
                        .result(t)
                        .build();
            }
        } else {
            errMessage = "访问UCS发生错误";
        }
        return UcsResult.<T>builder()
                .success(false)
                .message(errMessage)
                .result(null)
                .build();
    }

    private void prepareForUserRequest() {
        if (this.baseUrl == null || this.baseUrl.isEmpty()) {
            throw new IllegalArgumentException("请指定ucs服务的base url");
        }
        if (this.userToken == null || this.userToken.isEmpty()) {
            throw new IllegalArgumentException("请为ucs提供令牌");
        }
    }

    private String prepareForClientRequest(ClientAuthType clientAuthType) {
        if (this.baseUrl == null || this.baseUrl.isEmpty()) {
            throw new IllegalArgumentException("请指定ucs服务的base url");
        }
        switch (clientAuthType) {
            case TOKEN:
                if (this.clientToken == null || this.clientToken.isEmpty()) {
                    throw new IllegalArgumentException("请指定clientToken");
                }
                return this.clientToken;
            case ID_AND_SECRET:
                if (this.clientId == null || this.clientId.isEmpty() || this.clientSecret == null || this.clientSecret.isEmpty()) {
                    throw new IllegalArgumentException("请为ucs提供客户端id和秘钥，或者提供token");
                }
                return Base64.getEncoder().encodeToString((this.clientId + "@" + this.clientSecret).getBytes(StandardCharsets.UTF_8));
            default:
                throw new IllegalArgumentException("客户端认证方式[" + clientAuthType + "]错误");
        }
    }

    public static String generateRandomKey() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            result.append(ThreadLocalRandom.current().nextInt(0, 9));
        }
        return result.toString();
    }
}
