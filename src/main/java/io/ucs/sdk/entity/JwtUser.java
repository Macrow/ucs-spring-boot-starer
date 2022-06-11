package io.ucs.sdk.entity;

import lombok.*;

import java.util.Date;

/**
 * @author Macrow
 * @date 2022-03-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtUser {
    private String id;
    private String name;
    private String did;
    private String dn;
    private String iss;
    private Date iat;
    private Date exp;
    private String token;
}
