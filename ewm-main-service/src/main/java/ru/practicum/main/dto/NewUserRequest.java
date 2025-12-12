package ru.practicum.main.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewUserRequest {
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    String email;

    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}
