package ru.practicum.main.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.ParticipationRequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.model.ParticipationRequestStatus;
import ru.practicum.main.request.repository.ParticipationRequestRepository;
import ru.practicum.main.user.model.User;

@Service
public class ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    public ParticipationRequestService(ParticipationRequestRepository requestRepository,
                                       EventRepository eventRepository,
                                       UserService userService) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userService.getUserEntity(userId);
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(ParticipationRequestMapper::toDto)
                .toList();
    }

    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        User requester = userService.getUserEntity(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in own event");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event must be published");
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Request already exists");
        }

        long confirmed = requestRepository.countByEventAndStatus(event, ParticipationRequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmed);

        if (event.getParticipantLimit() != null && event.getParticipantLimit() != 0
                && confirmed >= event.getParticipantLimit()) {
            throw new ConflictException("The participant limit has been reached");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setEvent(event);
        request.setRequester(requester);

        boolean autoConfirm = !event.getRequestModeration() || event.getParticipantLimit() == 0;
        if (autoConfirm) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmed + 1);
        }

        ParticipationRequest saved = requestRepository.save(request);

        if (autoConfirm) {
            eventRepository.save(event);
        }

        return ParticipationRequestMapper.toDto(saved);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request with id=" + requestId + " was not found");
        }

        if (request.getStatus() == ParticipationRequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            long confirmed = requestRepository.countByEventAndStatus(event, ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(Math.max(0, confirmed - 1));
            eventRepository.save(event);
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);
        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        Event event = getOwnedEvent(userId, eventId);
        return requestRepository.findAllByEventId(event.getId())
                .stream()
                .map(ParticipationRequestMapper::toDto)
                .toList();
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest dto) {

        Event event = getOwnedEvent(userId, eventId);

        if (dto.getStatus() == null) {
            throw new ConflictException("Status is required");
        }

        if (dto.getRequestIds() == null || dto.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        ParticipationRequestStatus targetStatus;
        try {
            targetStatus = ParticipationRequestStatus.valueOf(dto.getStatus());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unknown status: " + dto.getStatus());
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(dto.getRequestIds());
        if (requests.size() != dto.getRequestIds().size()) {
            throw new NotFoundException("Some requests not found");
        }

        long confirmedCount = requestRepository.countByEventAndStatus(event, ParticipationRequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedCount);

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        for (ParticipationRequest request : requests) {

            if (!request.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Request does not belong to event");
            }

            if (request.getStatus() != ParticipationRequestStatus.PENDING) {
                throw new ConflictException("Request must have status PENDING");
            }

            if (targetStatus == ParticipationRequestStatus.CONFIRMED) {

                if (event.getParticipantLimit() != null && event.getParticipantLimit() != 0
                        && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new ConflictException("The participant limit has been reached");
                }

                request.setStatus(ParticipationRequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmed.add(ParticipationRequestMapper.toDto(requestRepository.save(request)));

                if (event.getParticipantLimit() != null && event.getParticipantLimit() != 0
                        && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    rejectPending(event, rejected);
                    break;
                }

            } else {
                request.setStatus(ParticipationRequestStatus.REJECTED);
                rejected.add(ParticipationRequestMapper.toDto(requestRepository.save(request)));
            }
        }

        eventRepository.save(event);
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    private void rejectPending(Event event, List<ParticipationRequestDto> rejected) {
        List<ParticipationRequest> pendingRequests =
                requestRepository.findAllByEventIdAndStatusIn(
                        event.getId(),
                        List.of(ParticipationRequestStatus.PENDING)
                );

        for (ParticipationRequest pending : pendingRequests) {
            pending.setStatus(ParticipationRequestStatus.REJECTED);
            rejected.add(ParticipationRequestMapper.toDto(requestRepository.save(pending)));
        }
    }

    private Event getOwnedEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        return event;
    }
}