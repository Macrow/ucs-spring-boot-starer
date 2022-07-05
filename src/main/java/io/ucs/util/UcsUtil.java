package io.ucs.util;

import io.ucs.exception.UcsAuthException;
import io.ucs.sdk.Constant;
import io.ucs.sdk.entity.JwtUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Macrow
 * @date 2022/06/11
 */
public class UcsUtil {
    public static JwtUser getJwtUser() {
        Object jwtUserObject = getRequest().getAttribute(Constant.REQUEST_JWT_USER_KEY);
        if (jwtUserObject == null) {
            throw new UcsAuthException("当前用户认证失败");
        }
        return (JwtUser) jwtUserObject;
    }

    public static List<String> getOrgIds() {
        Object orgIds = getRequest().getAttribute(Constant.REQUEST_ORG_IDS_KEY);
        if (orgIds == null) {
            return List.of();
        }
        return (List<String>) orgIds;
    }

    public static String generateRandomKey() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            result.append(ThreadLocalRandom.current().nextInt(0, 9));
        }
        return result.toString();
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(requestAttributes).getRequest();
    }
}
