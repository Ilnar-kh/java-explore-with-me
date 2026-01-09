package ru.practicum.main.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.main.dto.ApiError;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        logWarn(ex, request, HttpStatus.NOT_FOUND);
        return buildError(HttpStatus.NOT_FOUND, "The required object was not found.", ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request) {
        logWarn(ex, request, HttpStatus.CONFLICT);
        return buildError(HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.", ex.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        logWarn(ex, request, HttpStatus.BAD_REQUEST);
        return buildError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", ex.getMessage(), request);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            DateTimeParseException.class
    })
    public ResponseEntity<ApiError> handleValidation(Exception ex, HttpServletRequest request) {
        logWarn(ex, request, HttpStatus.BAD_REQUEST);
        return buildError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest request) {
        log.error("[ERROR_HANDLER] UNHANDLED exception -> {} {} | status={} | message={}",
                request.getMethod(), request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(), ex);

        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error.", ex.getMessage(), request);
    }

    private void logWarn(Exception ex, HttpServletRequest request, HttpStatus status) {
        log.warn("[ERROR_HANDLER] {} -> {} {} | status={} | message={}",
                ex.getClass().getSimpleName(),
                request.getMethod(), request.getRequestURI(),
                status.value(),
                ex.getMessage(), ex);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String reason, String message, HttpServletRequest request) {
        List<String> errors = message == null ? Collections.emptyList() : List.of(message);

        ApiError apiError = new ApiError(
                errors,
                message,
                reason,
                status.name(),
                LocalDateTime.now()
        );

        log.debug("[ERROR_HANDLER] response -> {} {} | status={} | reason={} | bodyMessage={}",
                request.getMethod(), request.getRequestURI(),
                status.value(), reason, message);

        return ResponseEntity.status(status).body(apiError);
    }
}