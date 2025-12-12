package ru.practicum.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;

import java.util.List;

@RestController
@RequestMapping("/events")
public class PublicEventController {

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@RequestParam(required = false) String text,
                                                         @RequestParam(required = false) List<Long> categories,
                                                         @RequestParam(required = false) Boolean paid,
                                                         @RequestParam(required = false) String rangeStart,
                                                         @RequestParam(required = false) String rangeEnd,
                                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                         @RequestParam(required = false) String sort,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
