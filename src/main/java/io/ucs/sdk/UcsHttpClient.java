package io.ucs.sdk;

import cn.hutool.json.JSONUtil;
import io.ucs.sdk.entity.JwtUser;
import io.ucs.sdk.entity.PermitResult;
import io.ucs.sdk.entity.UcsResult;
import kong.unirest.*;
import kong.unirest.json.JSONObject;

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
    private String accessCode;
    private String accessCodeHeader;
    private String randomKeyHeader;
    private String userTokenHeader;
    private String clientTokenHeader;
    private String userToken = null;
    private String clientId = null;
    private String clientSecret = null;
    private int timeout = Constant.DEFAULT_TIMEOUT_IN_SECONDS;

    public UcsHttpClient(String baseUrl, String accessCode) {
        this.baseUrl = baseUrl;
        this.accessCode = accessCode;
        this.accessCodeHeader = Constant.DefaultHeaderAccessCode;
        this.randomKeyHeader = Constant.DefaultHeaderRandomKey;
        this.userTokenHeader = Constant.BEARER_NAME;
        this.clientTokenHeader = Constant.CLIENT_HEADER_NAME;
    }

    public UcsHttpClient(String baseUrl, String accessCode, String clientId, String clientSecret) {
        this(baseUrl, accessCode);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
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
    public Client setUserToken(String token) {
        this.userToken = token;
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
        return request(JwtUser.class, Constant.ValidateJwtURL, "GET", null, RequestType.USER);
    }

    @Override
    public UcsResult<PermitResult> userValidatePermByOperation(String code, Boolean fulfillJwt) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("code", code);
        formData.put("fulfillJwt", fulfillJwt ? "1" : "0");
        return request(PermitResult.class, Constant.ValidatePermOperationByCodeURL, "POST", formData, RequestType.USER);
    }

    @Override
    public UcsResult<PermitResult> userValidatePermByAction(String service, String method, String path, Boolean fulfillJwt) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("service", service);
        formData.put("method", method);
        formData.put("path", path);
        formData.put("fulfillJwt", fulfillJwt ? "1" : "0");
        return request(PermitResult.class, Constant.ValidatePermActionURL, "POST", formData, RequestType.USER);
    }

    @Override
    public <T> UcsResult<T> userRequest(Class<T> klass, String method, String url, Map<String, Object> data) {
        return request(klass, url, method, data, RequestType.USER);
    }

    @Override
    public <T> UcsResult<T> clientRequest(Class<T> klass, String method, String url, Map<String, Object> data) {
        return request(klass, url, method, data, RequestType.CLIENT);
    }

    private <T> UcsResult<T> request(Class<T> klass, String url, String method, Map<String, Object> formData, RequestType requestType) {
        Map<String, String> headers = new HashMap<>();
        headers.put(this.accessCodeHeader, this.accessCode);
        headers.put(this.randomKeyHeader, getRandomKey(6));
        switch (requestType) {
            case USER:
                prepareForUserRequest();
                headers.put(this.userTokenHeader, Constant.BEARER_TYPE + " " + this.userToken);
                break;
            case CLIENT:
                prepareForClientRequest();
                headers.put(this.clientTokenHeader, Base64.getEncoder().encodeToString((this.clientId + "@" + this.clientSecret).getBytes(StandardCharsets.UTF_8)));
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

                T t = JSONUtil.toBean(result.toString(), klass);
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
            throw new IllegalArgumentException("please provide baseUrl first");
        }
        if (this.userToken == null || this.userToken.isEmpty()) {
            throw new IllegalArgumentException("please provide token first");
        }
    }

    private void prepareForClientRequest() {
        if (this.baseUrl == null || this.baseUrl.isEmpty()) {
            throw new IllegalArgumentException("please provide baseUrl first");
        }
        if (this.clientId == null || this.clientId.isEmpty() || this.clientSecret == null || this.clientSecret.isEmpty()) {
            throw new IllegalArgumentException("please provide client id/secret");
        }
    }

    private String getRandomKey(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(ThreadLocalRandom.current().nextInt(0, 9));
        }
        return result.toString();
    }
}
