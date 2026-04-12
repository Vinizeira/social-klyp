package com.github.devlucasjava.socialklyp.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.init-user")
public class InitUserProperties {

    private boolean enabled;
    private String username;
    private String email;
    private String password;
}