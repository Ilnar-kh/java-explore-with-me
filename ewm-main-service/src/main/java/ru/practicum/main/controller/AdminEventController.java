package ru.practicum.main.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.service.EventService;
import ru.practicum.main.util.DateTimeUtils;

@Validated
@RestController
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        LocalDateTime start = (rangeStart == null) ? null : DateTimeUtils.parse(rangeStart);
        LocalDateTime end = (rangeEnd == null) ? null : DateTimeUtils.parse(rangeEnd);

        return ResponseEntity.ok(
                eventService.getEventsAdmin(users, states, categories, start, end, from, size)
        );
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest dto
    ) {
        return ResponseEntity.ok(eventService.updateEventAdmin(eventId, dto));
    }
}