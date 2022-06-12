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
    @UcsPermByCode(code = "UCS_USER_LIST")
    @GetMapping("/perm-by-code")
    public void permByCode() {

    }

    /**
     * 可直接进行用户级别、应用级别的调用
     */
    @UcsAuth
    @GetMapping("/invoke")
    public void client() {
        UcsResult<Object> clientRes = ucsHttpClient.clientRequest(Object.class, "POST", "/api/v1/ucs/client/validate", null);
        log.info(clientRes.getSuccess().toString());
        UcsResult<Object> userRes = ucsHttpClient.setUserToken(UcsUtil.getJwtUser().getToken()).userRequest(Object.class, "GET", "/api/v1/ucs/users", null);
        log.info(userRes.getSuccess().toString());
    }
}
```
