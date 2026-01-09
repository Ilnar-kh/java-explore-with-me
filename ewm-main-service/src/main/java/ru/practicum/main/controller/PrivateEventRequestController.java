package ru.practicum.main.controller;

import jakarta.validation.Valid;
import java.util.List;

import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.main.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.service.ParticipationRequestService;

@Validated
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateEventRequestController {

    private final ParticipationRequestService participationRequestService;

    public PrivateEventRequestController(ParticipationRequestService participationRequestService) {
        this.participationRequestService = participationRequestService;
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {

        return ResponseEntity.ok(participationRequestService.getEventParticipants(userId, eventId));
    }

    @PatchMapping
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest dto) {

        return ResponseEntity.ok(participationRequestService.changeRequestStatus(userId, eventId, dto));
    }
}