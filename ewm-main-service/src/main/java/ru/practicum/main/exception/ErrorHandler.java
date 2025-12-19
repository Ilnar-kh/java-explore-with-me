package ru.practicum.main.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.main.dto.ApiError;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "The required object was not found.", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return buildError(HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.", ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", ex.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            DateTimeParseException.class
    })
    public ResponseEntity<ApiError> handleValidation(Exception ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", ex.getMessage());
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String reason, String message) {
        List<String> errors = message == null ? Collections.emptyList() : List.of(message);
        ApiError apiError = ApiError.builder()
                .errors(errors)
                .reason(reason)
                .message(message)
                .status(status.name())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(apiError);
    }
}
