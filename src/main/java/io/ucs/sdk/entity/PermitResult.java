package io.ucs.sdk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Macrow
 * @date 2022-03-20
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class PermitResult {
    private Boolean permit;
    private JwtUser user;
    private List<String> orgIds;
}