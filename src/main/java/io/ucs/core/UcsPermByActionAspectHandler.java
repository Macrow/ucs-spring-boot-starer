package io.ucs.core;

import io.ucs.annotation.UcsPermByAction;
import io.ucs.config.UcsConfig;
import io.ucs.exception.UcsPermException;
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
public class UcsPermByActionAspectHandler {
    final UcsHttpClient ucsHttpClient;
    final UcsConfig ucsConfig;

    @Around(value = "@annotation(ucsPermByAction)")
    public Object around(ProceedingJoinPoint joinPoint, UcsPermByAction ucsPermByAction) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        String token = request.getHeader(ucsConfig.getUserTokenHeader());
        if (token != null && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring("bearer ".length());
            String method = ucsPermByAction.method();
            String path = ucsPermByAction.path();
            if (method.isEmpty()) {
                method = request.getMethod();
            }
            if (path.isEmpty()) {
                path = request.getRequestURI();
            }
            UcsResult<PermitResult> res = ucsHttpClient.setUserToken(token).userValidatePermByAction(ucsConfig.getAppServiceName(), method, path);
            if (res.getSuccess()) {
                if (!res.getResult().getPermit()) {
                    throw new UcsPermException("UCS权限验证失败");
                }
            } else {
                throw new UcsPermException(res.getMessage());
            }
        } else {
            throw new UcsPermException("令牌错误");
        }
        return joinPoint.proceed();
    }
}
