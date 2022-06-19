# ucs-spring-boot-starter
[![](https://jitpack.io/v/Macrow/ucs-spring-boot-starter.svg)](https://jitpack.io/#Macrow/ucs-spring-boot-starter)

用于将```ucs```集成到```SpringBoot```的开发包

## 快速开始

### 添加安装源
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### 安装，请指定版本
```
<dependency>
    <groupId>com.github.Macrow</groupId>
    <artifactId>ucs-spring-boot-starter</artifactId>
    <version>${ucs-spring-boot-starter.version}</version>
</dependency>
```

### 配置文件
```yaml
# application.yaml
server:
  port: 8080

ucs:
  enable: true
  # your app service name
  app-service-name: test
  # ucs service base url
  service-base-url: http://localhost:8019
  access-code: 1A2B3C4D
  # 应用级别调用使用的clientId和clientSecret
  client-id: wsTDJzgAKg
  client-secret: 123456
```

### 自定义Handler
```java
@Slf4j
@Component
public class CustomAfterHandler implements Handler {
    @Override
    public void handle(JwtUser jwtUser) {
      log.info("CustomAfterHandler -> " + jwtUser.getName());
    }
}
```

### 权限校验

```java
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
        UcsResult<Object> clientRes = ucsHttpClient.clientRequest(Object.class, "GET", "/api/v1/ucs/client/validate", null, ClientAuthType.ID_AND_SECRET);
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
```
