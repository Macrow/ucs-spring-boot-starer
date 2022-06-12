package io.ucs.core;

import io.ucs.annotation.UcsPermByCode;
import io.ucs.config.UcsConfig;
import io.ucs.exception.UcsPermException;
import io.ucs.sdk.Constant;
import io.ucs.sdk.UcsHttpClient;
import io.ucs.sdk.entity.PermitResult;
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
 *
 * @author Macrow
 * @date 2022/06/11
 */
@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class UcsPermByCodeAspectHandler {
    final UcsHttpClient ucsHttpClient;
    final UcsConfig ucsConfig;

    @Around(value = "@annotation(ucsPermByCode)")
    public Object around(ProceedingJoinPoint joinPoint, UcsPermByCode ucsPermByCode) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        String token = request.getHeader(ucsConfig.getUserTokenHeader());
        if (token != null && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring("bearer ".length());
            UcsResult<PermitResult> res = ucsHttpClient.setUserToken(token).userValidatePermByOperation(ucsPermByCode.code(), ucsPermByCode.fulfillJwt());
            if (res.getSuccess()) {
                if (ucsPermByCode.fulfillJwt()) {
                    request.setAttribute(Constant.REQUEST_JWT_USER_KEY, res.getResult().getUser());
                }
                if (!res.getResult().getPermit()) {
                    throw new UcsPermException("UCS权限验证失败");
                }
            } else {
                throw new RuntimeException(res.getMessage());
            }
        } else {
            throw new UcsPermException("令牌错误");
        }
        return joinPoint.proceed();
    }
}
