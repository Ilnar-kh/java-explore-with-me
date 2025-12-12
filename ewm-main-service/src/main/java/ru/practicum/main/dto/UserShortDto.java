package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserShortDto {
    @NotNull
    Long id;

    @NotBlank
    @Size(min = 1, max = 250)
    String name;
}
