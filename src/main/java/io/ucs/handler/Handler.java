package io.ucs.handler;

import io.ucs.sdk.entity.JwtUser;

import java.util.List;

public interface Handler {
    void handle(JwtUser jwtUser, List<String> orgIds);
}
