package ru.practicum.main.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.main.service.EventService;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@PathVariable Long userId,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(eventService.getUserEvents(userId, from, size));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
        return ResponseEntity.status(201).body(eventService.addEvent(userId, dto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getUserEvent(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequest dto) {
        return ResponseEntity.ok(eventService.updateUserEvent(userId, eventId, dto));
    }
}
