package io.ucs.sdk;

/**
 * @author Macrow
 * @date 2022-03-16
 */
public interface Constant {
    String TIMEOUT_MSG = "timeout";
    String MSG_UNKNOWN = "未知错误";
    String MSG_HTTP_FAILED = "网络请求失败";
    String MSG_UNAUTHORIZED = "权限不足";

    String DEFAULT_HEADER_RANDOM_KEY = "Random-Key";
    String DEFAULT_ACCESS_CODE = "Access-Code";
    String DEFAULT_USER_HEADER_NAME = "Authorization";
    String DEFAULT_CLIENT_HEADER_NAME = "Client-Authorization";
    String DEFAULT_BEARER_TYPE = "Bearer";

    String ValidateJwtURL = "/api/v1/ucs/current/jwt";
    String ValidateClientURL = "/api/v1/ucs/client/validate";
    String ValidatePermOperationByCodeURL = "/api/v1/ucs/current/check-operation";
    String ValidatePermActionURL = "/api/v1/ucs/current/check-action";

    int DEFAULT_TIMEOUT_IN_SECONDS = 3;
    String REQUEST_JWT_USER_KEY = "__UCS_JWT_USER__";
    String REQUEST_ORG_IDS_KEY = "__UCS_ORG_IDS__";
}
