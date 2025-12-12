package ru.practicum.main.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LocationDto {
    @NotNull
    Double lat;
    @NotNull
    Double lon;
}
