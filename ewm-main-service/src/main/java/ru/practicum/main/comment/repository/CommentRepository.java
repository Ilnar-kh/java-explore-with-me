package ru.practicum.main.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.main.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByEventIdOrderByCreatedOnAsc(Long eventId, Pageable pageable);

    Page<Comment> findAllByAuthorIdOrderByCreatedOnDesc(Long authorId, Pageable pageable);
}
