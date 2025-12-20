package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CompilationDto {

    @NotNull
    private final List<EventShortDto> events;

    @NotNull
    private final Long id;

    @NotNull
    private final Boolean pinned;

    @NotBlank
    private final String title;

    public CompilationDto(List<EventShortDto> events, Long id, Boolean pinned, String title) {
        this.events = events;
        this.id = id;
        this.pinned = pinned;
        this.title = title;
    }

    public List<EventShortDto> getEvents() {
        return events;
    }

    public Long getId() {
        return id;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public String getTitle() {
        return title;
    }
}