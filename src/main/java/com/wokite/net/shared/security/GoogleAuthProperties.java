package com.wokite.net.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.google")
public record GoogleAuthProperties(
        String clientId
) {
}