package ru.practicum.main.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ApiError {
    List<String> errors;
    String message;
    String reason;
    String status;
    LocalDateTime timestamp;
}
