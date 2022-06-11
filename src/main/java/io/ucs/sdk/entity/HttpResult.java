package io.ucs.sdk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Macrow
 * @date 2022-03-17
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class HttpResult<T> {
    private T result;
    private Integer code;
    private String message;
}
