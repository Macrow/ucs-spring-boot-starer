package io.ucs.core;

import cn.hutool.extra.spring.SpringUtil;
import io.ucs.annotation.UcsPermByCode;
import io.ucs.config.UcsConfig;
import io.ucs.exception.UcsAuthException;
import io.ucs.exception.UcsPermException;
import io.ucs.handler.Handler;
import io.ucs.sdk.Constant;
import io.ucs.sdk.RequestType;
import io.ucs.sdk.UcsHttpClient;
import io.ucs.sdk.entity.PermitResult;
import io.ucs.sdk.entity.UcsMetaInfo;
import io.ucs.sdk.entity.UcsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Macrow
 * @date 2022/06/11
 */
@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class UcsPermByCodeAspectHandler {
    final UcsMetaInfoExtractor ucsMetaInfoExtractor;
    final UcsHttpClient ucsHttpClient;
    final UcsConfig ucsConfig;

    @Around(value = "@annotation(ucsPermByCode)")
    public Object around(ProceedingJoinPoint joinPoint, UcsPermByCode ucsPermByCode) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        UcsMetaInfo ucsMetaInfo = ucsMetaInfoExtractor.extract(request, RequestType.USER);
        UcsResult<PermitResult> res;
        try {
            res = ucsHttpClient
                    .setUserToken(ucsMetaInfo.getUserToken())
                    .setAccessCode(ucsMetaInfo.getAccessCode())
                    .setRandomKey(ucsMetaInfo.getRandomKey())
                    .userValidatePermByOperation(ucsPermByCode.code(), ucsPermByCode.fulfillJwt(), ucsPermByCode.fulfillOrgIds());
        } catch (Exception e) {
            throw new UcsAuthException(e.getMessage());
        }
        if (res.getSuccess()) {
            if (ucsPermByCode.fulfillJwt()) {
                request.setAttribute(Constant.REQUEST_JWT_USER_KEY, res.getResult().getUser());
            }
            if (ucsPermByCode.fulfillOrgIds()) {
                request.setAttribute(Constant.REQUEST_ORG_IDS_KEY, res.getResult().getOrgIds());
            }
            if (ucsPermByCode.afterHandler() != Handler.class) {
                Object handler = null;
                try {
                    handler = SpringUtil.getBean(ucsPermByCode.afterHandler());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("afterHandler参数错误:" + e.getMessage());
                }
                if (handler instanceof Handler) {
                    ((Handler) handler).handle(res.getResult().getUser(), res.getResult().getOrgIds());
                } else {
                    throw new UcsAuthException("afterHandler参数错误:该bean必须实现Handler接口");
                }
            }
            if (!res.getResult().getPermit()) {
                throw new UcsPermException("UCS权限验证失败");
            }
        } else {
            throw new RuntimeException(res.getMessage());
        }

        return joinPoint.proceed();
    }
}
