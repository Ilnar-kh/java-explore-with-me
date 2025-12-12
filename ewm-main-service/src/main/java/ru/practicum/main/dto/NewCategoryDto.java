package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewCategoryDto {
    @NotBlank
    @Size(min = 1, max = 50)
    String name;
}
