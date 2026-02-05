/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : EmailConfig
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.email")
public class EmailConfig {

    private String verifyPath;
    private String resetPath;

}
