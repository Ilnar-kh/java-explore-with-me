package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventShortDto {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDto category;
    @NotNull
    Long confirmedRequests;
    @NotBlank
    String eventDate;
    @NotNull
    Long id;
    @NotNull
    UserShortDto initiator;
    @NotNull
    Boolean paid;
    @NotBlank
    String title;
    Long views;
}
