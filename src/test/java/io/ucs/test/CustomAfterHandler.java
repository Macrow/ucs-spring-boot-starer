package io.ucs.test;

import cn.hutool.json.JSONUtil;
import io.ucs.handler.Handler;
import io.ucs.sdk.entity.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Macrow
 * @date 2022/08/01
 */
@Slf4j
@Component
public class CustomAfterHandler implements Handler {
    @Override
    public void handle(JwtUser jwtUser, List<String> orgIds) {
      log.info("CustomAfterHandler: jwtUser -> " + jwtUser.getName());
      log.info("CustomAfterHandler: orgIds -> " + JSONUtil.toJsonStr(orgIds));
    }
}
