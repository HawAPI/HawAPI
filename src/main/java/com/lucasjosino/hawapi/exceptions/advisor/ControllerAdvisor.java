package com.lucasjosino.hawapi.exceptions.advisor;

import com.lucasjosino.hawapi.exceptions.BadRequestException;
import com.lucasjosino.hawapi.exceptions.InternalServerErrorException;
import com.lucasjosino.hawapi.exceptions.ItemNotFoundException;
import com.lucasjosino.hawapi.exceptions.SaveConflictException;
import com.lucasjosino.hawapi.exceptions.auth.RoleBadRequestException;
import com.lucasjosino.hawapi.exceptions.auth.UserConflictException;
import com.lucasjosino.hawapi.exceptions.auth.UserNotFoundException;
import com.lucasjosino.hawapi.exceptions.auth.UserUnauthorizedException;
import com.lucasjosino.hawapi.exceptions.specification.OperatorNotFoundException;
import com.lucasjosino.hawapi.models.http.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private final ExceptionResponse response = new ExceptionResponse();

    private static String getParams(Map<String, String[]> params) {
        if (params.isEmpty()) return null;

        StringJoiner joiner = new StringJoiner(",", "{", "}");
        for (String key : params.keySet()) {
            joiner.add(key + "=" + Arrays.toString(params.get(key)));
        }

        return joiner.toString();
    }

    @ExceptionHandler({InternalServerErrorException.class, Exception.class, NullPointerException.class})
    public ResponseEntity<Object> handleInternalServerErrorException(
            InternalServerErrorException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.INTERNAL_SERVER_ERROR, servletRequest);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFoundException(
            ItemNotFoundException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.NOT_FOUND, servletRequest);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.BAD_REQUEST, servletRequest);
    }

    @ExceptionHandler(SaveConflictException.class)
    public ResponseEntity<Object> handleConflictException(
            SaveConflictException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.CONFLICT, servletRequest);
    }

    // Specification

    @ExceptionHandler(OperatorNotFoundException.class)
    public ResponseEntity<Object> handleOperatorNotFoundException(
            OperatorNotFoundException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.NOT_FOUND, servletRequest);
    }

    // User exceptions

    @ExceptionHandler(RoleBadRequestException.class)
    public ResponseEntity<Object> handleRoleBadRequestException(
            RoleBadRequestException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.BAD_REQUEST, servletRequest);
    }

    @ExceptionHandler(UserConflictException.class)
    public ResponseEntity<Object> handleUserConflictException(
            UserConflictException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.CONFLICT, servletRequest);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.NOT_FOUND, servletRequest);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<Object> handleUserUnauthorizedException(
            UserUnauthorizedException ex,
            HttpServletRequest servletRequest
    ) {
        return handleExceptionInternal(ex, HttpStatus.UNAUTHORIZED, servletRequest);
    }

    private ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            HttpStatus status,
            HttpServletRequest servletRequest
    ) {
        response.setCode(status.value());
        response.setStatus(status.getReasonPhrase());
        response.setMethod(servletRequest.getMethod());
        response.setCause(ex.getCause() != null ? ex.getCause().getLocalizedMessage() : null);
        response.setMessage(ex.getMessage());
        response.setTimestamps(LocalDateTime.now());
        response.setUrl(servletRequest.getRequestURI());
        response.setParams(getParams(servletRequest.getParameterMap()));
        return new ResponseEntity<>(response, status);
    }
}
