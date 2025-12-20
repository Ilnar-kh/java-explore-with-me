package ru.practicum.main.mapper;

import ru.practicum.main.category.model.Category;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.location.model.Location;
import ru.practicum.main.user.model.User;
import ru.practicum.main.util.DateTimeUtils;

public final class EventMapper {

    private EventMapper() {
        // utility class
    }

    public static Event toEntity(NewEventDto dto, Category category, User initiator, Location location) {
        Event event = new Event();

        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(DateTimeUtils.parse(dto.getEventDate()));
        event.setTitle(dto.getTitle());

        event.setCategory(category);
        event.setInitiator(initiator);
        event.setLocation(location);

        // defaults
        event.setPaid(dto.getPaid() != null && dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());

        // safe defaults (чтобы не вылетать на null)
        if (event.getConfirmedRequests() == null) {
            event.setConfirmedRequests(0L);
        }
        if (event.getViews() == null) {
            event.setViews(0L);
        }

        return event;
    }

    public static EventFullDto toFullDto(Event event, Long views) {
        Long resolvedViews = (views != null)
                ? views
                : (event.getViews() == null ? 0L : event.getViews());

        Long confirmedRequests = (event.getConfirmedRequests() == null)
                ? 0L
                : event.getConfirmedRequests();

        // ВАЖНО: порядок аргументов ровно как в твоём EventFullDto (см. подсказку IDE)
        return new EventFullDto(
                event.getAnnotation(),                        // String annotation
                CategoryMapper.toDto(event.getCategory()),    // CategoryDto category
                confirmedRequests,                            // Long confirmedRequests
                DateTimeUtils.format(event.getCreatedOn()),   // String createdOn
                event.getDescription(),                       // String description
                DateTimeUtils.format(event.getEventDate()),   // String eventDate
                event.getId(),                                // Long id
                UserMapper.toShortDto(event.getInitiator()),  // UserShortDto initiator
                LocationMapper.toDto(event.getLocation()),    // LocationDto location
                event.getPaid(),                              // Boolean paid
                event.getParticipantLimit(),                  // Integer participantLimit
                DateTimeUtils.format(event.getPublishedOn()), // String publishedOn (может быть null)
                event.getRequestModeration(),                 // Boolean requestModeration
                event.getState().name(),                      // String state
                event.getTitle(),                             // String title
                resolvedViews                                 // Long views
        );
    }

    public static EventShortDto toShortDto(Event event, Long views) {
        Long resolvedViews = (views != null)
                ? views
                : (event.getViews() == null ? 0L : event.getViews());

        Long confirmedRequests = (event.getConfirmedRequests() == null)
                ? 0L
                : event.getConfirmedRequests();

        // Порядок под твой EventShortDto (как в исходном варианте проекта)
        return new EventShortDto(
                event.getAnnotation(),                        // annotation
                CategoryMapper.toDto(event.getCategory()),    // category
                confirmedRequests,                            // confirmedRequests
                DateTimeUtils.format(event.getEventDate()),   // eventDate
                event.getId(),                                // id
                UserMapper.toShortDto(event.getInitiator()),  // initiator
                event.getPaid(),                              // paid
                event.getTitle(),                             // title
                resolvedViews                                 // views
        );
    }
}