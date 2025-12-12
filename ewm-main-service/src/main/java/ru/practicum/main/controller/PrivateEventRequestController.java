package ru.practicum.main.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateEventRequestController {

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(@PathVariable Long userId,
                                                                              @PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(@PathVariable Long userId,
                                                                              @PathVariable Long eventId,
                                                                              @Valid @RequestBody EventRequestStatusUpdateRequest dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
