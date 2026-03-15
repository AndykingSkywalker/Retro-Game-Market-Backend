package org.example.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.example.rest.dto.ApiErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDto> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ApiErrorDto dto = new ApiErrorDto(status.value(), status.getReasonPhrase(), ex.getReason(), request.getRequestURI());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorDto dto = new ApiErrorDto(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrorDto dto = new ApiErrorDto(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorDto dto = new ApiErrorDto(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorDto dto = new ApiErrorDto(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(dto, status);
    }
}
