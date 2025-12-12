package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CompilationDto {
    List<EventShortDto> events;

    @NotNull
    Long id;

    @NotNull
    Boolean pinned;

    @NotBlank
    String title;
}
