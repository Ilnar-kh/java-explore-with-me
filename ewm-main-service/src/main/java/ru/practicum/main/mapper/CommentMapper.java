package ru.practicum.main.mapper;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.model.User;
import ru.practicum.main.util.DateTimeUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {

    public static Comment toEntity(NewCommentDto dto, User author, Event event) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                UserMapper.toShortDto(comment.getAuthor()),
                comment.getEvent().getId(),
                DateTimeUtils.format(comment.getCreatedOn()),
                DateTimeUtils.format(comment.getUpdatedOn())
        );
    }
}
