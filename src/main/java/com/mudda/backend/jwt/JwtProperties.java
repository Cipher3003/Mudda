/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : JwtProperties
 * Author  : Vikas Kumar
 * Created : 12-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    @NotBlank
    private String secretKey;

    @Positive
    private long accessTtlMin;

    @Positive
    private long refreshTtlDays;

}
