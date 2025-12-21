package ru.practicum.main.event.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;

public interface EventRepository extends JpaRepository<Event, Long> {

    // ---------- Admin ----------
    @Query("""
            SELECT e
            FROM Event e
            WHERE (:#{#users == null or #users.isEmpty()} = true OR e.initiator.id IN :users)
              AND (:#{#states == null or #states.isEmpty()} = true OR e.state IN :states)
              AND (:#{#categories == null or #categories.isEmpty()} = true OR e.category.id IN :categories)
              AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
              AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
            """)
    Page<Event> findAllByAdminFilters(@Param("users") List<Long> users,
                                      @Param("states") List<EventState> states,
                                      @Param("categories") List<Long> categories,
                                      @Param("rangeStart") LocalDateTime rangeStart,
                                      @Param("rangeEnd") LocalDateTime rangeEnd,
                                      Pageable pageable);

    // ---------- Public (JPQL, без nativeQuery — чтобы Sort/Pageable не ломал SQL) ----------
    @Query("""
            SELECT e
            FROM Event e
            WHERE e.state = ru.practicum.main.event.model.EventState.PUBLISHED
              AND (:categoriesEmpty = true OR e.category.id IN :categories)
              AND (:paid IS NULL OR e.paid = :paid)
              AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
              AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
              AND (
                    :onlyAvailable = false
                    OR e.participantLimit = 0
                    OR COALESCE(e.confirmedRequests, 0) < e.participantLimit
                  )
            """)
    Page<Event> findAllPublishedByFiltersNoText(@Param("categoriesEmpty") boolean categoriesEmpty,
                                                @Param("categories") List<Long> categories,
                                                @Param("paid") Boolean paid,
                                                @Param("rangeStart") LocalDateTime rangeStart,
                                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                                @Param("onlyAvailable") boolean onlyAvailable,
                                                Pageable pageable);

    @Query("""
            SELECT e
            FROM Event e
            WHERE e.state = ru.practicum.main.event.model.EventState.PUBLISHED
              AND (
                    :text IS NULL
                    OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))
                  )
              AND (:categoriesEmpty = true OR e.category.id IN :categories)
              AND (:paid IS NULL OR e.paid = :paid)
              AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
              AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
              AND (
                    :onlyAvailable = false
                    OR e.participantLimit = 0
                    OR COALESCE(e.confirmedRequests, 0) < e.participantLimit
                  )
            """)
    Page<Event> findAllPublishedByFiltersWithText(@Param("text") String text,
                                                  @Param("categoriesEmpty") boolean categoriesEmpty,
                                                  @Param("categories") List<Long> categories,
                                                  @Param("paid") Boolean paid,
                                                  @Param("rangeStart") LocalDateTime rangeStart,
                                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                                  @Param("onlyAvailable") boolean onlyAvailable,
                                                  Pageable pageable);

    // ---------- Остальное ----------
    Collection<Event> findAllByIdIn(Collection<Long> ids);

    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByInitiatorId(Long initiatorId);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);
}