package io.ucs.sdk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Macrow
 * @date 2022/07/05
 */
@Data
@Builder
@AllArgsConstructor
public class UcsMetaInfo {
    private String userToken;
    private String clientToken;
    private String accessCode;
    private String randomKey;
}
