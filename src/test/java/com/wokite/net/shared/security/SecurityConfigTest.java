package com.wokite.net.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTest {

    @Test
    void passwordEncoderShouldBeUsable() {
        PasswordEncoder encoder = new SecurityConfig().passwordEncoder();

        String rawPassword = "Secret123!";
        String encodedPassword = encoder.encode(rawPassword);

        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}