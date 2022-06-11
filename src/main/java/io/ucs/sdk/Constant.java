package io.ucs.sdk;

/**
 * @author Macrow
 * @date 2022-03-16
 */
interface Constant {
    String TIMEOUT_MSG = "timeout";
    String MSG_UNKNOWN = "未知错误";
    String MSG_HTTP_FAILED = "网络请求失败";
    String MSG_UNAUTHORIZED = "权限不足";

    String DefaultHeaderRandomKey = "Random-Key";
    String DefaultHeaderAccessCode = "Access-Code";

    String ValidateJwtURL = "/api/v1/ucs/current/jwt";
    String ValidatePermOperationByCodeURL = "/api/v1/ucs/current/check-operation";
    String ValidatePermActionURL = "/api/v1/ucs/current/check-action";

    int DEFAULT_TIMEOUT_IN_SECONDS = 3;
    String BEARER_NAME = "Authorization";
    String CLIENT_HEADER_NAME = "Client-Authorization";
    String BEARER_TYPE = "Bearer";
}
