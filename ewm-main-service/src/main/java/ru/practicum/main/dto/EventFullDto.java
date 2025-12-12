package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventFullDto {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDto category;
    @NotNull
    Long confirmedRequests;
    String createdOn;
    @NotBlank
    String description;
    @NotBlank
    String eventDate;
    Long id;
    @NotNull
    UserShortDto initiator;
    @NotNull
    LocationDto location;
    @NotNull
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    @NotBlank
    String title;
    Long views;
}
