package io.ucs.test;

import cn.hutool.json.JSONUtil;
import io.ucs.annotation.UcsAuth;
import io.ucs.annotation.UcsClientAuth;
import io.ucs.annotation.UcsPermByAction;
import io.ucs.annotation.UcsPermByCode;
import io.ucs.sdk.ClientAuthType;
import io.ucs.sdk.UcsHttpClient;
import io.ucs.sdk.entity.JwtUser;
import io.ucs.sdk.entity.UcsResult;
import io.ucs.util.UcsUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Macrow
 * @date 2022/06/11
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    final UcsHttpClient ucsHttpClient;

    /**
     * 校验用户登录是否合法，并返回JwtUser对象
     * 如果验证失败，会抛出UcsAuthException异常
     */
    @UcsAuth
    @GetMapping("/auth")
    public void auth() {
        JwtUser jwtUser = UcsUtil.getJwtUser();
        log.info(jwtUser.getName());
        log.info("orgIds: " + JSONUtil.toJsonStr(UcsUtil.getOrgIds()));
    }

    /**
     * 校验应用级调用是否合法
     * 如果验证失败，会抛出UcsAuthException异常
     */
    @UcsClientAuth
    @GetMapping("/client-auth")
    public void clientAuth() {
        log.info("client OK");
        // 本次调用继续使用：外部传来的应用令牌
        UcsResult<Object> clientRes = ucsHttpClient.clientRequest(Object.class, "GET", "/api/v1/ucs/client/validate", null, ClientAuthType.TOKEN);
        log.info(clientRes.getSuccess().toString());
        // 本次调用改为使用：自己配置的应用令牌
        clientRes = ucsHttpClient.clientRequest(Object.class, "GET", "/api/v1/ucs/client/validate", null, ClientAuthType.ID_AND_SECRET);
        log.info(clientRes.getSuccess().toString());
    }

    /**
     * 校验用户登录是否合法，并返回JwtUser对象
     * 如果验证失败，会抛出UcsAuthException异常
     */
    @UcsAuth(afterHandler = CustomAfterHandler.class)
    @GetMapping("/auth-with-custom-handler")
    public void authWithCustomHandler() {

    }

    /**
     * 校验用户是否拥有Action权限，如果不指定method和path，会自动识别
     * 如果验证失败，会抛出UcsPermException
     */
    @UcsPermByAction
    @GetMapping("/perm-by-action")
    public void permByAction() {
        log.info("orgIds: " + JSONUtil.toJsonStr(UcsUtil.getOrgIds()));
    }

    /**
     * 校验用户是否拥有Action权限，可手动指定method和path
     * 如果验证失败，会抛出UcsPermException
     */
    @UcsPermByAction(method = "PUT", path = "/ok")
    @GetMapping("/perm-by-action2")
    public void permByAction2() {
        log.info("orgIds: " + JSONUtil.toJsonStr(UcsUtil.getOrgIds()));
    }

    /**
     * 校验用户是否拥有Operations权限，通过指定code进行验证
     * 如果验证失败，会抛出UcsPermException
     */
    @UcsPermByCode(code = "UCS_USER_LIST", afterHandler = CustomAfterHandler.class)
    @GetMapping("/perm-by-code")
    public void permByCode() {
        log.info(UcsUtil.getJwtUser().getName());
        log.info("orgIds: " + JSONUtil.toJsonStr(UcsUtil.getOrgIds()));
    }

    /**
     * 可直接进行用户级别的调用
     */
    @UcsAuth
    @GetMapping("/user-invoke")
    public void userRequest() {
        ParameterizedTypeReference<Operations> type = new ParameterizedTypeReference<>() {
        };
        UcsResult<Operations> userRes = ucsHttpClient
                .setUserToken(UcsUtil.getJwtUser().getToken())
                .setAccessCode("1A2B3C4D")
                .setRandomKey(UcsUtil.generateRandomKey())
                .userRequest(type.getType(), "GET", "/api/v1/ucs/current/operations", null);
        userRes.getResult().getItems().forEach(log::info);
    }

    /**
     * 可直接进行应用级别的调用
     */
    @GetMapping("/client-invoke")
    public void clientRequest() {
        UcsResult<Object> clientRes = ucsHttpClient.clientRequest(Object.class, "GET", "/api/v1/ucs/client/validate", null, ClientAuthType.ID_AND_SECRET);
        log.info(clientRes.getSuccess().toString());
    }

    @Data
    private static class Operations {
        private List<String> items;
    }
}
