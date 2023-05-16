package com.lucasjosino.hawapi.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleBadRequestException extends RuntimeException {

    public RoleBadRequestException() {
    }

    public RoleBadRequestException(String message) {
        super(message);
    }

    public RoleBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
