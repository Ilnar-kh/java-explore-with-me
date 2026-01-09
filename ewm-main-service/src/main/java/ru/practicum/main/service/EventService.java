package ru.practicum.main.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.dto.UpdateEventAdminRequest;
import ru.practicum.main.dto.UpdateEventUserRequest;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.util.DateTimeUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final LocationService locationService;
    private final StatsService statsService;

    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsAdmin(List<Long> users,
                                             List<EventState> states,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             int from,
                                             int size) {

        if (from < 0) throw new BadRequestException("from must be >= 0");
        if (size <= 0) throw new BadRequestException("size must be > 0");
        validateRange(rangeStart, rangeEnd);

        log.info("[ADMIN_EVENTS] incoming: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        Pageable pageable = PageRequest.of(from / size, size);

        Specification<Event> spec = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("initiator").get("id").in(users));
        }
        if (states != null && !states.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("state").in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("category").get("id").in(categories));
        }
        if (rangeStart != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        var page = eventRepository.findAll(spec, pageable);

        log.info("[ADMIN_EVENTS] result: totalElements={}, returned={}",
                page.getTotalElements(), page.getContent().size());

        if (page.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> views = resolveViews(page.getContent());

        return page.getContent().stream()
                .map(e -> EventMapper.toFullDto(e, views.getOrDefault(e.getId(), 0L)))
                .toList();
    }

    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        applyCommonUpdates(event,
                dto.getAnnotation(),
                dto.getDescription(),
                dto.getEventDate(),
                dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration(),
                dto.getTitle());

        if (dto.getCategory() != null) {
            event.setCategory(getCategory(dto.getCategory()));
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationService.save(dto.getLocation()));
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newDate = DateTimeUtils.parse(dto.getEventDate());
            if (newDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("Event date must be at least one hour in the future");
            }
            if (event.getPublishedOn() != null && newDate.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new BadRequestException("Event date must be at least one hour after publication");
            }
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "PUBLISH_EVENT" -> {
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
                    }
                    LocalDateTime publishTime = LocalDateTime.now();
                    if (event.getEventDate().isBefore(publishTime.plusHours(1))) {
                        throw new BadRequestException("Event date must be at least one hour after publication");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(publishTime);
                }
                case "REJECT_EVENT" -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot reject published event");
                    }
                    event.setState(EventState.CANCELED);
                }
                default -> throw new BadRequestException("Unknown state action: " + dto.getStateAction());
            }
        }

        Event saved = eventRepository.save(event);
        Map<Long, Long> views = resolveViews(List.of(saved));
        return EventMapper.toFullDto(saved, views.getOrDefault(saved.getId(), 0L));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               boolean onlyAvailable,
                                               String sort,
                                               int from,
                                               int size,
                                               HttpServletRequest request) {

        try {
            statsService.hit(request);
        } catch (Exception e) {
            log.debug("[PUBLIC_EVENTS] stats hit failed: {}", e.getMessage());
        }

        if (from < 0) throw new BadRequestException("from must be >= 0");
        if (size <= 0) throw new BadRequestException("size must be > 0");
        if (sort != null && !sort.equals("EVENT_DATE") && !sort.equals("VIEWS")) {
            throw new BadRequestException("Unknown sort: " + sort);
        }

        LocalDateTime start = (rangeStart == null) ? LocalDateTime.now() : DateTimeUtils.parse(rangeStart);
        LocalDateTime end = (rangeEnd == null) ? null : DateTimeUtils.parse(rangeEnd);
        validateRange(start, end);

        String normText = (text == null || text.isBlank()) ? null : text.toLowerCase();

        boolean categoriesEmpty = (categories == null || categories.isEmpty());
        List<Long> safeCategories = categoriesEmpty ? List.of(-1L) : categories;

        PageRequest pageRequest = PageRequest.of(from / size, size);
        boolean sortByEventDate = "EVENT_DATE".equals(sort);

        log.info("[PUBLIC_EVENTS] incoming: text={}, categoriesEmpty={}, categories={}, paid={}, start={}, end={}, onlyAvailable={}, sort={}, from={}, size={}",
                normText, categoriesEmpty, categories, paid, start, end, onlyAvailable, sort, from, size);

        List<Event> events = (normText == null)
                ? (sortByEventDate
                ? eventRepository.findAllPublishedNoTextOrderByEventDate(categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
                : eventRepository.findAllPublishedNoTextOrderById(categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
        ).getContent()
                : (sortByEventDate
                ? eventRepository.findAllPublishedWithTextOrderByEventDate(normText, categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
                : eventRepository.findAllPublishedWithTextOrderById(normText, categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
        ).getContent();

        if (events == null || events.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> views = resolveViews(events);

        List<EventShortDto> result = events.stream()
                .map(e -> EventMapper.toShortDto(e, views.getOrDefault(e.getId(), 0L)))
                .toList();

        if ("VIEWS".equals(sort)) {
            result = result.stream()
                    .sorted((a, b) -> Long.compare(
                            b.getViews() == null ? 0 : b.getViews(),
                            a.getViews() == null ? 0 : a.getViews()
                    ))
                    .toList();
        }

        return result;
    }

    @Transactional(readOnly = true)
    public EventFullDto getPublicEvent(Long id, HttpServletRequest request) {
        try {
            statsService.hit(request);
        } catch (Exception e) {
            log.debug("[PUBLIC_EVENT] stats hit failed: {}", e.getMessage());
        }

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + id + " was not found");
        }

        Map<Long, Long> views = resolveViews(List.of(event));
        return EventMapper.toFullDto(event, views.getOrDefault(event.getId(), 0L));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        if (from < 0) throw new BadRequestException("from must be >= 0");
        if (size <= 0) throw new BadRequestException("size must be > 0");

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest).getContent();

        if (events == null || events.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> views = resolveViews(events);

        return events.stream()
                .map(e -> EventMapper.toShortDto(e, views.getOrDefault(e.getId(), 0L)))
                .toList();
    }

    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        LocalDateTime eventDate = DateTimeUtils.parse(dto.getEventDate());
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event date must be at least two hours in the future");
        }

        User initiator = userService.getUserEntity(userId);
        Category category = getCategory(dto.getCategory());

        Event event = EventMapper.toEntity(dto, category, initiator, locationService.save(dto.getLocation()));
        Event saved = eventRepository.save(event);

        Map<Long, Long> views = resolveViews(List.of(saved));
        return EventMapper.toFullDto(saved, views.getOrDefault(saved.getId(), 0L));
    }

    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Map<Long, Long> views = resolveViews(List.of(event));
        return EventMapper.toFullDto(event, views.getOrDefault(event.getId(), 0L));
    }

    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newDate = DateTimeUtils.parse(dto.getEventDate());
            if (newDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Event date must be at least two hours in the future");
            }
        }

        applyCommonUpdates(event,
                dto.getAnnotation(),
                dto.getDescription(),
                dto.getEventDate(),
                dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration(),
                dto.getTitle());

        if (dto.getCategory() != null) {
            event.setCategory(getCategory(dto.getCategory()));
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationService.save(dto.getLocation()));
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "SEND_TO_REVIEW" -> event.setState(EventState.PENDING);
                case "CANCEL_REVIEW" -> event.setState(EventState.CANCELED);
                default -> throw new BadRequestException("Unknown state action: " + dto.getStateAction());
            }
        }

        Event saved = eventRepository.save(event);
        Map<Long, Long> views = resolveViews(List.of(saved));
        return EventMapper.toFullDto(saved, views.getOrDefault(saved.getId(), 0L));
    }

    private void applyCommonUpdates(Event event,
                                    String annotation,
                                    String description,
                                    String eventDate,
                                    Boolean paid,
                                    Integer participantLimit,
                                    Boolean requestModeration,
                                    String title) {

        if (participantLimit != null && participantLimit < 0) {
            throw new BadRequestException("participantLimit must be >= 0");
        }

        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (eventDate != null) event.setEventDate(DateTimeUtils.parse(eventDate));
        if (paid != null) event.setPaid(paid);
        if (participantLimit != null) event.setParticipantLimit(participantLimit);
        if (requestModeration != null) event.setRequestModeration(requestModeration);
        if (title != null) event.setTitle(title);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }

    private Map<Long, Long> resolveViews(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        Map<String, Long> stats;
        try {
            stats = statsService.getViews(uris);
            if (stats == null) stats = Map.of();
        } catch (Exception e) {
            log.debug("[VIEWS] stats getViews failed: {}", e.getMessage());
            stats = Map.of();
        }

        Map<Long, Long> result = new HashMap<>();
        for (Event event : events) {
            String uri = "/events/" + event.getId();
            long v = stats.getOrDefault(uri, event.getViews() == null ? 0L : event.getViews());
            result.put(event.getId(), v);
        }
        return result;
    }

    private void validateRange(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new BadRequestException("rangeEnd must be after rangeStart");
        }
    }
}