package io.ucs.util;

import io.ucs.sdk.Constant;
import io.ucs.sdk.entity.JwtUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Macrow
 * @date 2022/06/11
 */
public class UcsUtil {
    public static JwtUser getJwtUser() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        return (JwtUser) request.getAttribute(Constant.REQUEST_JWT_USER_KEY);
    }
}
