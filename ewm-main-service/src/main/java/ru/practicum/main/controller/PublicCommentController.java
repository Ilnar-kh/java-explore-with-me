package ru.practicum.main.controller;

import java.util.List;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.service.CommentService;

@Validated
@RestController
@RequestMapping
public class PublicCommentController {

    private final CommentService commentService;

    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/events/{eventId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByEventViaEvents(
            @PathVariable @Positive Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        return ResponseEntity.ok(commentService.getPublicComments(eventId, from, size));
    }

    @GetMapping("/comments/event/{eventId}")
    public ResponseEntity<List<CommentDto>> getCommentsByEventViaComments(
            @PathVariable @Positive Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        return ResponseEntity.ok(commentService.getPublicComments(eventId, from, size));
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
            @PathVariable @Positive Long commentId) {

        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }
}
