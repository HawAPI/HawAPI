package com.lucasjosino.hawapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException() {
        super("Not found!");
    }

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Class<?> oClass) {
        super(oClass.getSimpleName().replace("Model", "") + " not found!");
    }
}
