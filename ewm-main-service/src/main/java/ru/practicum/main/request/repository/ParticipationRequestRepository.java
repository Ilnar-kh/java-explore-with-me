package ru.practicum.main.request.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.model.ParticipationRequestStatus;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventIdAndStatusIn(Long eventId, Collection<ParticipationRequestStatus> statuses);

    long countByEventAndStatus(Event event, ParticipationRequestStatus status);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);
}
