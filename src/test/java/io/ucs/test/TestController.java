package io.ucs.test;

import io.ucs.annotation.UcsAuth;
import io.ucs.annotation.UcsClientAuth;
import io.ucs.annotation.UcsPermByAction;
import io.ucs.annotation.UcsPermByCode;
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
    }

    /**
     * 校验应用级调用是否合法
     * 如果验证失败，会抛出UcsAuthException异常
     */
    @UcsClientAuth
    @GetMapping("/client-auth")
    public void clientAuth() {
        log.info("client OK");
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

    }

    /**
     * 校验用户是否拥有Action权限，可手动指定method和path
     * 如果验证失败，会抛出UcsPermException
     */
    @UcsPermByAction(method = "PUT", path = "/ok")
    @GetMapping("/perm-by-action2")
    public void permByAction2() {

    }

    /**
     * 校验用户是否拥有Operations权限，通过指定code进行验证
     * 如果验证失败，会抛出UcsPermException
     */
    @UcsPermByCode(code = "UCS_USER_LIST", afterHandler = CustomAfterHandler.class)
    @GetMapping("/perm-by-code")
    public void permByCode() {
        log.info(UcsUtil.getJwtUser().getName());
    }

    /**
     * 可直接进行用户级别、应用级别的调用
     */
    @UcsAuth
    @GetMapping("/invoke")
    public void client() {
        UcsResult<Object> clientRes = ucsHttpClient.clientRequest(Object.class, "POST", "/api/v1/ucs/client/validate", null);
        log.info(clientRes.getSuccess().toString());
        ParameterizedTypeReference<Ids> type = new ParameterizedTypeReference<>() {};
        UcsResult<Ids> userRes = ucsHttpClient.setUserToken(UcsUtil.getJwtUser().getToken()).userRequest(type.getType(), "GET", "/api/v1/ucs/current/org-ids", null);
        userRes.getResult().getIds().forEach(log::info);
        userRes = ucsHttpClient.setUserToken(UcsUtil.getJwtUser().getToken()).userRequest(Ids.class, "GET", "/api/v1/ucs/current/org-ids", null);
        userRes.getResult().getIds().forEach(log::info);
    }

    @Data
    private static class Ids {
        private List<String> ids;
    }
}
