package io.ucs.core;

import io.ucs.annotation.UcsClientAuth;
import io.ucs.config.UcsConfig;
import io.ucs.exception.UcsAuthException;
import io.ucs.sdk.ClientAuthType;
import io.ucs.sdk.UcsHttpClient;
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
 * @date 2022/06/17
 */
@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class UcsClientAuthAspectHandler {
    final UcsConfig ucsConfig;
    final UcsHttpClient ucsHttpClient;

    @Around(value = "@annotation(ucsClientAuth)")
    public Object around(ProceedingJoinPoint joinPoint, UcsClientAuth ucsClientAuth) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        String token = request.getHeader(ucsConfig.getClientTokenHeader());
        if (token != null && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring("bearer ".length());
            UcsResult<Void> res;
            try {
                res = ucsHttpClient.setClientToken(token).clientValidate(ClientAuthType.TOKEN);
            } catch (Exception e) {
                throw new UcsAuthException(e.getMessage());
            }
            if (!res.getSuccess()) {
                throw new UcsAuthException(res.getMessage());
            }
        } else {
            throw new UcsAuthException("权限验证失败：请求令牌格式错误");
        }
        return joinPoint.proceed();
    }
}
