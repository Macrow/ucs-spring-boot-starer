package io.ucs.handler;

import io.ucs.sdk.entity.JwtUser;

public interface Handler {
    void handle(JwtUser jwtUser);
}
