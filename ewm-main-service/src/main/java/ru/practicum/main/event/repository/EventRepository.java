package ru.practicum.main.event.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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

    @Query("""
        SELECT e
        FROM Event e
        WHERE e.state = 'PUBLISHED'
            AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
            AND (:#{#categories == null or #categories.isEmpty()} = true OR e.category.id IN :categories)
            AND (:paid IS NULL OR e.paid = :paid)
            AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
            AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
            AND (:onlyAvailable = FALSE OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)
    """)
    Page<Event> findAllPublishedByFilters(@Param("text") String text,
                                          @Param("categories") List<Long> categories,
                                          @Param("paid") Boolean paid,
                                          @Param("rangeStart") LocalDateTime rangeStart,
                                          @Param("rangeEnd") LocalDateTime rangeEnd,
                                          @Param("onlyAvailable") boolean onlyAvailable,
                                          Pageable pageable);

    Collection<Event> findAllByIdIn(Collection<Long> ids);
}
