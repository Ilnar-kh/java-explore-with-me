package ru.practicum.main.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    private final List<String> errors;
    private final String message;
    private final String reason;
    private final String status;
    private final LocalDateTime timestamp;

    public ApiError(List<String> errors,
                    String message,
                    String reason,
                    String status,
                    LocalDateTime timestamp) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}