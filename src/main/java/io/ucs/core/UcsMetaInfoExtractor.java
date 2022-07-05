package io.ucs.core;

import io.ucs.config.UcsConfig;
import io.ucs.exception.UcsAuthException;
import io.ucs.sdk.RequestType;
import io.ucs.sdk.entity.UcsMetaInfo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Macrow
 * @date 2022/07/05
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class UcsMetaInfoExtractor {
    private UcsConfig ucsConfig;

    public UcsMetaInfo extract(HttpServletRequest request, RequestType requestType) {
        String accessCode = request.getHeader(ucsConfig.getAccessCodeHeader());
        String randomKey = request.getHeader(ucsConfig.getRandomKeyHeader());
        String token = null;
        String clientIdAndSecret = null;
        switch (requestType) {
            case USER:
                token = request.getHeader(ucsConfig.getUserTokenHeader());
                if (token != null && token.startsWith(ucsConfig.getUserTokenHeaderSchema() + " ")) {
                    token = token.substring(ucsConfig.getUserTokenHeaderSchema().length() + 1);
                } else {
                    throw new UcsAuthException("权限验证失败：请求令牌格式错误");
                }
                break;
            case CLIENT:
                clientIdAndSecret = request.getHeader(ucsConfig.getClientTokenHeader());
                if (clientIdAndSecret != null && clientIdAndSecret.startsWith(ucsConfig.getClientTokenHeaderSchema() + " ")) {
                    clientIdAndSecret = clientIdAndSecret.substring(ucsConfig.getClientTokenHeaderSchema().length() + 1);
                } else {
                    throw new UcsAuthException("权限验证失败：请求令牌格式错误");
                }
                break;
            default:
                throw new UcsAuthException("权限验证失败：未知的鉴权类型");
        }
        return UcsMetaInfo.builder()
                .userToken(token)
                .clientToken(clientIdAndSecret)
                .accessCode(accessCode)
                .randomKey(randomKey)
                .build();
    }
}
