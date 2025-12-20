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

        event.setPaid(dto.getPaid() != null && dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());

        return event;
    }

    public static EventFullDto toFullDto(Event event, Long views) {
        Long resolvedViews = (views == null ? event.getViews() : views);

        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toDto(event.getCategory()),
                event.getConfirmedRequests(),
                DateTimeUtils.format(event.getCreatedOn()),
                event.getDescription(),
                DateTimeUtils.format(event.getEventDate()),
                event.getId(),
                UserMapper.toShortDto(event.getInitiator()),
                LocationMapper.toDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                DateTimeUtils.format(event.getPublishedOn()),
                event.getRequestModeration(),
                event.getState().name(),
                event.getTitle(),
                resolvedViews
        );
    }

    public static EventShortDto toShortDto(Event event, Long views) {
        Long resolvedViews = (views == null ? event.getViews() : views);

        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toDto(event.getCategory()),
                event.getConfirmedRequests(),
                DateTimeUtils.format(event.getEventDate()),
                event.getId(),
                UserMapper.toShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                resolvedViews
        );
    }
}