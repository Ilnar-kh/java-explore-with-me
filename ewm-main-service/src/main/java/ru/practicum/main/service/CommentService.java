package ru.practicum.main.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.dto.UpdateCommentDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CommentMapper;
import ru.practicum.main.user.model.User;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository,
                          EventRepository eventRepository,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getPublicComments(Long eventId, int from, int size) {
        validatePaging(from, size);
        Event event = getPublishedEvent(eventId);

        return commentRepository.findAllByEventIdOrderByCreatedOnAsc(
                        event.getId(),
                        PageRequest.of(from / size, size))
                .map(CommentMapper::toDto)
                .getContent();
    }

    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto dto) {
        User author = userService.getUserEntity(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event must be published");
        }

        Comment saved = commentRepository.save(CommentMapper.toEntity(dto, author, event));
        return CommentMapper.toDto(saved);
    }

    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto dto) {
        Comment comment = getCommentEntityById(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found");
        }

        comment.setText(dto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDto(saved);
    }

    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = getCommentEntityById(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = getCommentEntityById(commentId);
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        validatePaging(from, size);

        userService.getUserEntity(userId);

        return commentRepository.findAllByAuthorIdOrderByCreatedOnDesc(
                        userId,
                        PageRequest.of(from / size, size))
                .map(CommentMapper::toDto)
                .getContent();
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        return CommentMapper.toDto(getCommentEntityById(commentId));
    }

    private Comment getCommentEntityById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
    }

    private Event getPublishedEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        return event;
    }

    private void validatePaging(int from, int size) {
        if (from < 0) {
            throw new BadRequestException("from must be >= 0");
        }
        if (size <= 0) {
            throw new BadRequestException("size must be > 0");
        }
    }
}