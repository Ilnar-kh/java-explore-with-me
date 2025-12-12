package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    String description;

    @NotBlank
    String eventDate;

    @NotNull
    LocationDto location;

    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;

    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}
