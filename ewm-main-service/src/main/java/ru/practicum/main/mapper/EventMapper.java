package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.location.model.Location;
import ru.practicum.main.user.model.User;
import ru.practicum.main.util.DateTimeUtils;

@UtilityClass
public class EventMapper {

    public Event toEntity(NewEventDto dto, Category category, User initiator, Location location) {
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

    public EventFullDto toFullDto(Event event, Long views) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(DateTimeUtils.format(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(DateTimeUtils.format(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .location(LocationMapper.toDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(DateTimeUtils.format(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .title(event.getTitle())
                .views(views == null ? event.getViews() : views)
                .build();
    }

    public EventShortDto toShortDto(Event event, Long views) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(DateTimeUtils.format(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views == null ? event.getViews() : views)
                .build();
    }
}
