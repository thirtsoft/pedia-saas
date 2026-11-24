package com.wokite.net.shared.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class SecurityConfigTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("pediasaas_test")
                    .withUsername("pediasaas")
                    .withPassword("pediasaas");

    @DynamicPropertySource
    static void configureDatasource(
            DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername
        );

        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword
        );

        registry.add(
                "spring.flyway.url",
                POSTGRES::getJdbcUrl
        );

        registry.add(
                "spring.flyway.user",
                POSTGRES::getUsername
        );

        registry.add(
                "spring.flyway.password",
                POSTGRES::getPassword
        );
    }

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void doitChargerLaConfigurationDeSecurite() {

        assertThat(securityFilterChain)
                .isNotNull();
    }

    @Test
    void doitChargerLeFiltreJwt() {

        assertThat(jwtAuthenticationFilter)
                .isNotNull();
    }

    @Test
    void doitChargerLeServiceJwt() {

        assertThat(jwtService)
                .isNotNull();
    }

    @Test
    void doitChargerLesProprietesJwt() {

        assertThat(jwtProperties)
                .isNotNull();

        assertThat(jwtProperties.secret())
                .isNotBlank();

        assertThat(jwtProperties.expiration())
                .isNotNull();
    }
}