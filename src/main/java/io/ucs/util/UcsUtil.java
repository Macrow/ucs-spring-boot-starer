package io.ucs.util;

import io.ucs.sdk.entity.JwtUser;

/**
 * @author Macrow
 * @date 2022/06/11
 */
public class UcsUtil {
    private static JwtUser jwtUser;

    public static JwtUser getJwtUser() {
        return jwtUser;
    }

    public static void setJwtUser(JwtUser jwtUser) {
        UcsUtil.jwtUser = jwtUser;
    }
}
