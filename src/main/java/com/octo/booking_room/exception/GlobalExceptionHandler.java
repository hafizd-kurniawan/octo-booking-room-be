package com.octo.booking_room.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.octo.booking_room.dto.ApiResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Centralized exception handling for REST controllers.  Converts validation
 * failures and runtime errors into a consistent {@link ApiResponse} payload
 * so the client can parse error messages uniformly.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        ApiResponse<Map<String, String>> body = new ApiResponse<>(
                "error", "Validation failed", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(EmailAlreadyExists.class)
    public ResponseEntity<ApiResponse<String>> emailAlreadyExists(EmailAlreadyExists ex) {
        ApiResponse<String> body = new ApiResponse<>(
                "error", ex.getMessage(), null);
        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAll(Exception ex) {
        ApiResponse<String> body = new ApiResponse<>(
                "error", "An unexpected error occurred", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
