package io.ucs.test;

import io.ucs.annotation.UcsAuth;
import io.ucs.annotation.UcsPermByAction;
import io.ucs.annotation.UcsPermByCode;
import io.ucs.annotation.UcsSkip;
import io.ucs.config.UcsConfig;
import io.ucs.core.UcsMetaInfoExtractor;
import io.ucs.exception.UcsAuthException;
import io.ucs.exception.UcsPermException;
import io.ucs.sdk.Constant;
import io.ucs.sdk.RequestType;
import io.ucs.sdk.UcsHttpClient;
import io.ucs.sdk.entity.PermitResult;
import io.ucs.sdk.entity.UcsMetaInfo;
import io.ucs.sdk.entity.UcsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Macrow
 * @date 2022/08/01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UcsInterceptor implements HandlerInterceptor {
    final UcsMetaInfoExtractor ucsMetaInfoExtractor;
    final UcsHttpClient ucsHttpClient;
    final UcsConfig ucsConfig;

    private static final Boolean fulfillJwt = true;
    private static final Boolean fulfillOrgIds = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method handlerMethod = ((HandlerMethod) handler).getMethod();
        if (handlerMethod.isAnnotationPresent(UcsSkip.class)
                || handlerMethod.isAnnotationPresent(UcsAuth.class)
                || handlerMethod.isAnnotationPresent(UcsPermByAction.class)
                || handlerMethod.isAnnotationPresent(UcsPermByCode.class)) {
            return true;
        }

        UcsMetaInfo ucsMetaInfo = ucsMetaInfoExtractor.extract(request, RequestType.USER);
        String method = request.getMethod();
        String path = request.getRequestURI();

        UcsResult<PermitResult> res;
        try {
            res = ucsHttpClient
                    .setUserToken(ucsMetaInfo.getUserToken())
                    .setAccessCode(ucsMetaInfo.getAccessCode())
                    .setRandomKey(ucsMetaInfo.getRandomKey())
                    .userValidatePermByAction(ucsConfig.getAppServiceName(), method, path, fulfillJwt, fulfillOrgIds);
        } catch (Exception e) {
            throw new UcsAuthException(e.getMessage());
        }
        if (res.getSuccess()) {
            if (fulfillJwt) {
                request.setAttribute(Constant.REQUEST_JWT_USER_KEY, res.getResult().getUser());
            }
            if (fulfillOrgIds) {
                request.setAttribute(Constant.REQUEST_ORG_IDS_KEY, res.getResult().getOrgIds());
            }
            if (!res.getResult().getPermit()) {
                throw new UcsPermException("UCS权限验证失败");
            }
        } else {
            throw new UcsPermException(res.getMessage());
        }

        return true;
    }
}
