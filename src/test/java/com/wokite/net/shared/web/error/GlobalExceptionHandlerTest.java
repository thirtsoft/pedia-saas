package com.wokite.net.shared.web.error;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapResourceNotFoundTo404() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiError> response = handler.handleNotFound(
                new ResourceNotFoundException("Ressource introuvable."),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        assertThat(response.getBody().message()).isEqualTo("Ressource introuvable.");
        assertThat(response.getBody().errors()).isEqualTo(List.of());
    }

    @Test
    void shouldMapConflictTo409() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ApiError> response = handler.handleConflict(
                new ConflictException("Conflit."),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.CONFLICT);
    }
}