package com.github.devlucasjava.socialklyp.delivery.rest.advice;

import com.github.devlucasjava.socialklyp.delivery.rest.advice.dto.FieldErrorDTO;
import com.github.devlucasjava.socialklyp.delivery.rest.advice.dto.ResponseErrorsDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
@RestControllerAdvice
public class GlobalHandlerException {

    private ResponseEntity<ResponseErrorsDTO> build(HttpStatus status, String message, List<FieldErrorDTO> errors) {
        return ResponseEntity.status(status).body(
                ResponseErrorsDTO.builder()
                        .code(status.value())
                        .message(message)
                        .errors(errors)
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorsDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<FieldErrorDTO> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> new FieldErrorDTO(e.getDefaultMessage(), e.getField()))
                .toList();

        return build(HttpStatus.BAD_REQUEST, "Validation error", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseErrorsDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseErrorsDTO> handleConstraintViolation(ConstraintViolationException ex) {

        List<FieldErrorDTO> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> new FieldErrorDTO(v.getMessage(), v.getPropertyPath().toString()))
                .toList();

        return build(HttpStatus.BAD_REQUEST, "Constraint violation", errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseErrorsDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "Database constraint violation", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseErrorsDTO> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Access denied", null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseErrorsDTO> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidOrExpiredTokenException.class)
    public ResponseEntity<ResponseErrorsDTO> handleInvalidOrExpiredTokenException(InvalidOrExpiredTokenException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseErrorsDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseErrorsDTO> handleConflictException(ConflictException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    // - TODO: DISABLE FOR DEVELOPMENT NOW, ENABLE LATER
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ResponseErrorsDTO> handleGenericException(Exception ex) {
//        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", null);
//    }
}