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

    @Query("""
            SELECT e
            FROM Event e
            WHERE (:usersEmpty = true OR e.initiator.id IN :users)
              AND (:statesEmpty = true OR e.state IN :states)
              AND (:categoriesEmpty = true OR e.category.id IN :categories)
              AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
              AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
            """)
    Page<Event> findAllByAdminFilters(@Param("usersEmpty") boolean usersEmpty,
                                      @Param("users") List<Long> users,
                                      @Param("statesEmpty") boolean statesEmpty,
                                      @Param("states") List<EventState> states,
                                      @Param("categoriesEmpty") boolean categoriesEmpty,
                                      @Param("categories") List<Long> categories,
                                      @Param("rangeStart") LocalDateTime rangeStart,
                                      @Param("rangeEnd") LocalDateTime rangeEnd,
                                      Pageable pageable);

    @Query(
            value = """
                    SELECT e.*
                    FROM events e
                    WHERE e.state = 'PUBLISHED'
                      AND (:categoriesEmpty = true OR e.category_id IN (:categories))
                      AND (:paid IS NULL OR e.paid = :paid)
                      AND e.event_date >= COALESCE(CAST(:rangeStart AS timestamp), e.event_date)
                      AND e.event_date <= COALESCE(CAST(:rangeEnd   AS timestamp), e.event_date)
                      AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)
                    ORDER BY e.id ASC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM events e
                    WHERE e.state = 'PUBLISHED'
                      AND (:categoriesEmpty = true OR e.category_id IN (:categories))
                      AND (:paid IS NULL OR e.paid = :paid)
                      AND e.event_date >= COALESCE(CAST(:rangeStart AS timestamp), e.event_date)
                      AND e.event_date <= COALESCE(CAST(:rangeEnd   AS timestamp), e.event_date)
                      AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)
                    """,
            nativeQuery = true
    )
    Page<Event> findAllPublishedNoTextOrderById(@Param("categoriesEmpty") boolean categoriesEmpty,
                                                @Param("categories") List<Long> categories,
                                                @Param("paid") Boolean paid,
                                                @Param("rangeStart") LocalDateTime rangeStart,
                                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                                @Param("onlyAvailable") boolean onlyAvailable,
                                                Pageable pageable);

    @Query(
            value = """
                    SELECT e.*
                    FROM events e
                    WHERE e.state = 'PUBLISHED'
                      AND (:categoriesEmpty = true OR e.category_id IN (:categories))
                      AND (:paid IS NULL OR e.paid = :paid)
                      AND e.event_date >= COALESCE(CAST(:rangeStart AS timestamp), e.event_date)
                      AND e.event_date <= COALESCE(CAST(:rangeEnd   AS timestamp), e.event_date)
                      AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)
                    ORDER BY e.event_date ASC, e.id ASC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM events e
                    WHERE e.state = 'PUBLISHED'
                      AND (:categoriesEmpty = true OR e.category_id IN (:categories))
                      AND (:paid IS NULL OR e.paid = :paid)
                      AND e.event_date >= COALESCE(CAST(:rangeStart AS timestamp), e.event_date)
                      AND e.event_date <= COALESCE(CAST(:rangeEnd   AS timestamp), e.event_date)
                      AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)
                    """,
            nativeQuery = true
    )
    Page<Event> findAllPublishedNoTextOrderByEventDate(@Param("categoriesEmpty") boolean categoriesEmpty,
                                                       @Param("categories") List<Long> categories,
                                                       @Param("paid") Boolean paid,
                                                       @Param("rangeStart") LocalDateTime rangeStart,
                                                       @Param("rangeEnd") LocalDateTime rangeEnd,
                                                       @Param("onlyAvailable") boolean onlyAvailable,
                                                       Pageable pageable);

    @Query(
            value = """
                    SELECT e.*
                    FROM events e
                    WHERE e.state = 'PUBLISHED'
                      AND (
                            :text IS NULL
                            OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                            OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))
                          )
                      AND (:categoriesEmpty = true OR e.category_id IN (:categories))
                      AND (:paid IS NULL OR e.paid = :paid)
                      AND e.event_date >= COALESCE(CAST(:rangeStart AS timestamp), e.event_date)
                      AND e.event_date <= COALESCE(CAST(:rangeEnd   AS timestamp), e.event_date)
                      AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)
                    ORDER BY e.id ASC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM events e
                    WHERE e.state = 'PUBLISHED'
                      AND (
                            :text IS NULL
                            OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                            OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))
                          )
                      AND (:categoriesEmpty = true OR e.category_id IN (:categories))
                      AND (:paid IS NULL OR e.paid = :paid)
                      AND e.event_date >= COALESCE(CAST(:rangeStart AS timestamp), e.event_date)
                      AND e.event_date <= COALESCE(CAST(:rangeEnd   AS timestamp), e.event_date)
                      AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)
                    """,
            nativeQuery = true
    )
    Page<Event> findAllPublishedWithTextOrderById(@Param("text") String text,
                                                  @Param("categoriesEmpty") boolean categoriesEmpty,
                                                  @Param("categories") List<Long> categories,
                                                  @Param("paid") Boolean paid,
                                                  @Param("rangeStart") LocalDateTime rangeStart,
                                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                                  @Param("onlyAvailable") boolean onlyAvailable,
                                                  Pageable pageable);

    @Query(
            value = """
                    SELECT e.*
                    FROM events e
                    WHERE e.state = 'PUBLISHED'
                      AND (
                            :text IS NULL
                            OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                            OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))
                          )
                      AND (:categoriesEmpty = true OR e.category_id IN (:categories))
                      AND (:paid IS NULL OR e.paid = :paid)
                      AND e.event_date >= COALESCE(CAST(:rangeStart AS timestamp), e.event_date)
                      AND e.event_date <= COALESCE(CAST(:rangeEnd   AS timestamp), e.event_date)
                      AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)
                    ORDER BY e.event_date ASC, e.id ASC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM events e
                    WHERE e.state = 'PUBLISHED'
                      AND (
                            :text IS NULL
                            OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                            OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))
                          )
                      AND (:categoriesEmpty = true OR e.category_id IN (:categories))
                      AND (:paid IS NULL OR e.paid = :paid)
                      AND e.event_date >= COALESCE(CAST(:rangeStart AS timestamp), e.event_date)
                      AND e.event_date <= COALESCE(CAST(:rangeEnd   AS timestamp), e.event_date)
                      AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)
                    """,
            nativeQuery = true
    )
    Page<Event> findAllPublishedWithTextOrderByEventDate(@Param("text") String text,
                                                         @Param("categoriesEmpty") boolean categoriesEmpty,
                                                         @Param("categories") List<Long> categories,
                                                         @Param("paid") Boolean paid,
                                                         @Param("rangeStart") LocalDateTime rangeStart,
                                                         @Param("rangeEnd") LocalDateTime rangeEnd,
                                                         @Param("onlyAvailable") boolean onlyAvailable,
                                                         Pageable pageable);

    Collection<Event> findAllByIdIn(Collection<Long> ids);

    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByInitiatorId(Long initiatorId);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);
}