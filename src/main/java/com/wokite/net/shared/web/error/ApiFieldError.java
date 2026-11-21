package com.wokite.net.shared.web.error;

public record ApiFieldError(
        String field,
        String message
) {
}