package com.wokite.net.shared.web.error;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        ErrorCode code,
        String message,
        String path,
        List<ApiFieldError> errors
) {
    public ApiError {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }
}