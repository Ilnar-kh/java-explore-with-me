package ru.practicum.mainservice.request.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.request.model.ParticipationRequest;
import ru.practicum.mainservice.request.model.ParticipationRequestStatus;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventIdAndStatusIn(Long eventId, Collection<ParticipationRequestStatus> statuses);

    long countByEventAndStatus(Event event, ParticipationRequestStatus status);
}
