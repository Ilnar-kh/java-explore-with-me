package ru.practicum.main.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.dto.UpdateCommentDto;
import ru.practicum.main.service.CommentService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/users/{userId}")
public class PrivateCommentController {

    private final CommentService commentService;

    public PrivateCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/events/{eventId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody NewCommentDto dto) {

        return ResponseEntity.status(201).body(commentService.addComment(userId, eventId, dto));
    }

    @PostMapping("/comments/{eventId}")
    public ResponseEntity<CommentDto> addCommentAlias(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody NewCommentDto dto) {

        return ResponseEntity.status(201).body(commentService.addComment(userId, eventId, dto));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId,
            @Valid @RequestBody UpdateCommentDto dto) {

        return ResponseEntity.ok(commentService.updateComment(userId, commentId, dto));
    }

    @PatchMapping("/comments/{eventId}/{commentId}")
    public ResponseEntity<CommentDto> updateCommentAlias(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long commentId,
            @Valid @RequestBody UpdateCommentDto dto) {

        return ResponseEntity.ok(commentService.updateComment(userId, commentId, dto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId) {

        commentService.deleteCommentByUser(userId, commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDto>> getMyComments(
            @PathVariable @Positive Long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {

        return ResponseEntity.ok(commentService.getUserComments(userId, from, size));
    }
}