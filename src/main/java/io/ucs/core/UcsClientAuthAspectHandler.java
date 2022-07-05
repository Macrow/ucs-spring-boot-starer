package io.ucs.core;

import io.ucs.annotation.UcsClientAuth;
import io.ucs.config.UcsConfig;
import io.ucs.exception.UcsAuthException;
import io.ucs.sdk.ClientAuthType;
import io.ucs.sdk.RequestType;
import io.ucs.sdk.UcsHttpClient;
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
 * @date 2022/06/17
 */
@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class UcsClientAuthAspectHandler {
    final UcsMetaInfoExtractor ucsMetaInfoExtractor;
    final UcsConfig ucsConfig;
    final UcsHttpClient ucsHttpClient;

    @Around(value = "@annotation(ucsClientAuth)")
    public Object around(ProceedingJoinPoint joinPoint, UcsClientAuth ucsClientAuth) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        UcsMetaInfo ucsMetaInfo = ucsMetaInfoExtractor.extract(request, RequestType.CLIENT);
        UcsResult<Void> res;
        try {
            res = ucsHttpClient
                    .setClientToken(ucsMetaInfo.getClientToken())
                    .setAccessCode(ucsMetaInfo.getAccessCode())
                    .setRandomKey(ucsMetaInfo.getRandomKey())
                    .clientValidate(ClientAuthType.TOKEN);
        } catch (Exception e) {
            throw new UcsAuthException(e.getMessage());
        }
        if (!res.getSuccess()) {
            throw new UcsAuthException(res.getMessage());
        }

        return joinPoint.proceed();
    }
}
