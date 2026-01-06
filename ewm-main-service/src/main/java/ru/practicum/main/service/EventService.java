package ru.practicum.main.service;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.dto.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.util.DateTimeUtils;
import ru.practicum.main.user.model.User;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final LocationService locationService;
    private final StatsService statsService;

    public EventService(EventRepository eventRepository,
                        CategoryRepository categoryRepository,
                        UserService userService,
                        LocationService locationService,
                        StatsService statsService) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.locationService = locationService;
        this.statsService = statsService;
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsAdmin(List<Long> users,
                                             List<String> states,
                                             List<Long> categories,
                                             String rangeStart,
                                             String rangeEnd,
                                             int from,
                                             int size) {
        List<EventState> stateEnums = parseStates(states);

        LocalDateTime start = rangeStart == null ? null : DateTimeUtils.parse(rangeStart);
        LocalDateTime end = rangeEnd == null ? null : DateTimeUtils.parse(rangeEnd);
        validateRange(start, end);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Event> events = eventRepository
                .findAllByAdminFilters(users, stateEnums, categories, start, end, pageRequest)
                .getContent();

        return events.stream()
                .map(event -> EventMapper.toFullDto(event, event.getViews()))
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
                case "PUBLISH_EVENT":
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot publish the event because it's not in the right state: "
                                + event.getState());
                    }
                    LocalDateTime publishTime = LocalDateTime.now();
                    if (event.getEventDate().isBefore(publishTime.plusHours(1))) {
                        throw new BadRequestException("Event date must be at least one hour after publication");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(publishTime);
                    break;

                case "REJECT_EVENT":
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot reject published event");
                    }
                    event.setState(EventState.CANCELED);
                    break;

                default:
                    throw new BadRequestException("Unknown state action: " + dto.getStateAction());
            }
        }

        Event saved = eventRepository.save(event);
        return EventMapper.toFullDto(saved, saved.getViews());
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsPublicFull(String text,
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
            // Игнорируем ошибки статистики, главное - вернуть события
        }

        LocalDateTime start;
        LocalDateTime end;

        try {
            start = (rangeStart == null) ? LocalDateTime.now() : DateTimeUtils.parse(rangeStart);
            end = (rangeEnd == null) ? null : DateTimeUtils.parse(rangeEnd);
            validateRange(start, end);
        } catch (Exception e) {
            // Если ошибка парсинга дат - используем дефолты
            start = LocalDateTime.now();
            end = null;
        }

        String normText = (text == null || text.isBlank()) ? null : text;

        boolean categoriesEmpty = (categories == null || categories.isEmpty());
        List<Long> safeCategories = categoriesEmpty ? new ArrayList<>() : categories;
        PageRequest pageRequest;
        try {
            pageRequest = PageRequest.of(from / size, size);
        } catch (Exception e) {
            pageRequest = PageRequest.of(0, 10); // дефолтные значения
        }

        boolean sortByEventDate = "EVENT_DATE".equals(sort);

        List<Event> events;
        try {
            if (normText == null) {
                events = (sortByEventDate
                        ? eventRepository.findAllPublishedNoTextOrderByEventDate(
                        categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
                        : eventRepository.findAllPublishedNoTextOrderById(
                        categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
                ).getContent();
            } else {
                events = (sortByEventDate
                        ? eventRepository.findAllPublishedWithTextOrderByEventDate(
                        normText, categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
                        : eventRepository.findAllPublishedWithTextOrderById(
                        normText, categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
                ).getContent();
            }
        } catch (Exception e) {
            // Если ошибка в репозитории - возвращаем пустой список
            events = new ArrayList<>();
        }

        // ГАРАНТИРУЕМ, что events не null
        if (events == null) {
            events = new ArrayList<>();
        }

        // ГАРАНТИРУЕМ, что результат не null
        if (events.isEmpty()) {
            return new ArrayList<>(); // Пустой список - это OK
        }

        Map<Long, Long> views = resolveViews(events);

        List<EventFullDto> result = new ArrayList<>();
        for (Event event : events) {
            try {
                EventFullDto dto = EventMapper.toFullDto(event, views.getOrDefault(event.getId(), 0L));
                if (dto != null) {
                    result.add(dto);
                }
            } catch (Exception e) {
                // Пропускаем проблемные события
                continue;
            }
        }

        if ("VIEWS".equals(sort)) {
            result.sort((a, b) -> {
                Long viewsA = a.getViews() != null ? a.getViews() : 0L;
                Long viewsB = b.getViews() != null ? b.getViews() : 0L;
                return viewsB.compareTo(viewsA);
            });
        }

        return result;
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
        statsService.hit(request);

        LocalDateTime start = (rangeStart == null) ? LocalDateTime.now() : DateTimeUtils.parse(rangeStart);
        LocalDateTime end = (rangeEnd == null) ? null : DateTimeUtils.parse(rangeEnd);
        validateRange(start, end);

        String normText = (text == null || text.isBlank()) ? null : text;

        boolean categoriesEmpty = (categories == null || categories.isEmpty());
        List<Long> safeCategories = categoriesEmpty ? List.of(-1L) : categories;

        PageRequest pageRequest = PageRequest.of(from / size, size);

        boolean sortByEventDate = "EVENT_DATE".equals(sort);

        List<Event> events;
        if (normText == null) {
            events = (sortByEventDate
                    ? eventRepository.findAllPublishedNoTextOrderByEventDate(
                    categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
                    : eventRepository.findAllPublishedNoTextOrderById(
                    categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
            ).getContent();
        } else {
            events = (sortByEventDate
                    ? eventRepository.findAllPublishedWithTextOrderByEventDate(
                    normText, categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
                    : eventRepository.findAllPublishedWithTextOrderById(
                    normText, categoriesEmpty, safeCategories, paid, start, end, onlyAvailable, pageRequest)
            ).getContent();
        }

        Map<Long, Long> views = resolveViews(events);

        List<EventShortDto> result = events.stream()
                .map(event -> EventMapper.toShortDto(event, views.getOrDefault(event.getId(), 0L)))
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
        statsService.hit(request);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + id + " was not found");
        }

        Map<Long, Long> views = resolveViews(List.of(event));
        long v = views.getOrDefault(event.getId(), 0L);

        return EventMapper.toFullDto(event, v);
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest).getContent();
        return events.stream()
                .map(event -> EventMapper.toShortDto(event, event.getViews()))
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

        return EventMapper.toFullDto(saved, saved.getViews());
    }

    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.toFullDto(event, event.getViews());
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
                case "SEND_TO_REVIEW":
                    event.setState(EventState.PENDING);
                    break;

                case "CANCEL_REVIEW":
                    event.setState(EventState.CANCELED);
                    break;

                default:
                    throw new BadRequestException("Unknown state action: " + dto.getStateAction());
            }
        }

        Event saved = eventRepository.save(event);
        return EventMapper.toFullDto(saved, saved.getViews());
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

        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(DateTimeUtils.parse(eventDate));
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            event.setTitle(title);
        }
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
                .map(event -> "/events/" + event.getId())
                .toList();

        Map<String, Long> stats = statsService.getViews(uris);

        Map<Long, Long> result = new HashMap<>();
        for (Event event : events) {
            long v = stats.getOrDefault("/events/" + event.getId(),
                    event.getViews() == null ? 0L : event.getViews());
            result.put(event.getId(), v);
        }
        return result;
    }

    private void validateRange(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new BadRequestException("rangeEnd must be after rangeStart");
        }
    }

    private List<EventState> parseStates(List<String> states) {
        if (states == null) {
            return null;
        }
        try {
            return states.stream()
                    .map(EventState::valueOf)
                    .toList();
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unknown state");
        }
    }
}