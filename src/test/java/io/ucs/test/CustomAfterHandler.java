package io.ucs.test;

import io.ucs.handler.Handler;
import io.ucs.sdk.entity.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAfterHandler implements Handler {
    @Override
    public void handle(JwtUser jwtUser) {
      log.info("CustomAfterHandler -> " + jwtUser.getName());
    }
}
