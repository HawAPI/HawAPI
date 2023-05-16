package com.lucasjosino.hawapi.exceptions;

import com.lucasjosino.hawapi.models.base.BaseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException() {
    }

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotFoundException(Class<? extends BaseDTO> oClass) {
        super(oClass.getSimpleName().replace("DTO", "") + " not found!");
    }

    public ItemNotFoundException(Class<? extends BaseDTO> oClass, Throwable cause) {
        super(oClass.getSimpleName().replace("DTO", "") + " not found!", cause);
    }
}
