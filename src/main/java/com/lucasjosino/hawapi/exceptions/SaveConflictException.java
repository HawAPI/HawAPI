package com.lucasjosino.hawapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class SaveConflictException extends RuntimeException {

    public SaveConflictException() {
    }

    public SaveConflictException(String message) {
        super(message);
    }

    public SaveConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}