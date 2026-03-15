package com.octo.booking_room.exception;

import java.util.Map;
import java.util.stream.Collectors;

import com.octo.booking_room.dto.ApiResponse;
import com.octo.booking_room.utils.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<WebResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        WebResponse<String> response = new WebResponse<>("error", ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // BARU: Handle AccessDeniedException (403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<WebResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        WebResponse<String> response = new WebResponse<>("error", "Access denied: " + ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // BARU: Handle UsernameNotFoundException (401 Unauthorized)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<WebResponse<String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        WebResponse<String> response = new WebResponse<>("error", "Invalid credentials", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<String>> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        WebResponse<String> response = new WebResponse<>("error", "An unexpected error occurred: " + ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

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

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> invalidCredentials(InvalidCredentialsException ex) {
        ApiResponse<String> body = new ApiResponse<>(
                "error", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}