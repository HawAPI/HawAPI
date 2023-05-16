package com.lucasjosino.hawapi.exceptions.specification;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class OperatorNotFoundException extends RuntimeException {

    public OperatorNotFoundException() {
        super("Not found!");
    }

    public OperatorNotFoundException(String message) {
        super(message);
    }

    public OperatorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}