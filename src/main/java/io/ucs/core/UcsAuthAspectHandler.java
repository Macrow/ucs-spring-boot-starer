package io.ucs.core;

import io.ucs.annotation.UcsAuth;
import io.ucs.config.UcsConfig;
import io.ucs.exception.UcsAuthException;
import io.ucs.sdk.UcsHttpClient;
import io.ucs.sdk.entity.JwtUser;
import io.ucs.sdk.entity.UcsResult;
import io.ucs.util.UcsUtil;
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
public class UcsAuthAspectHandler {
    final UcsConfig ucsConfig;
    final UcsHttpClient ucsHttpClient;

    @Around(value = "@annotation(ucsAuth)")
    public Object around(ProceedingJoinPoint joinPoint, UcsAuth ucsAuth) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        String token = request.getHeader(ucsConfig.getUserTokenHeader());
        if (token != null && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring("bearer ".length());
            UcsResult<JwtUser> res = ucsHttpClient.setUserToken(token).userValidateJwt();
            if (res.getSuccess()) {
                JwtUser jwtUser = res.getResult();
                jwtUser.setToken(token);
                UcsUtil.setJwtUser(jwtUser);
            } else {
                throw new UcsAuthException(res.getMessage());
            }
        }

        return joinPoint.proceed();
    }
}
