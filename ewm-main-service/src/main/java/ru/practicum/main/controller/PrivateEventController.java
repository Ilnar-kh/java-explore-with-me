package ru.practicum.main.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.dto.UpdateEventUserRequest;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@PathVariable Long userId,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequest dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
