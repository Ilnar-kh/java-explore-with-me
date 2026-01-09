package ru.practicum.main.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.dto.UserShortDto;
import ru.practicum.main.dto.LocationDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.location.model.Location;
import ru.practicum.main.user.model.User;
import ru.practicum.main.util.DateTimeUtils;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    public static Event toEntity(NewEventDto dto, Category category, User initiator, Location location) {
        Event event = new Event();

        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(DateTimeUtils.parse(dto.getEventDate()));
        event.setTitle(dto.getTitle());

        event.setCategory(category);
        event.setInitiator(initiator);
        event.setLocation(location);

        event.setPaid(Boolean.TRUE.equals(dto.getPaid()));
        event.setParticipantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());

        if (event.getConfirmedRequests() == null) {
            event.setConfirmedRequests(0L);
        }
        if (event.getViews() == null) {
            event.setViews(0L);
        }
        if (event.getState() == null) {
            event.setState(EventState.PENDING);
        }
        if (event.getCreatedOn() == null) {
            event.setCreatedOn(LocalDateTime.now());
        }

        return event;
    }

    public static EventFullDto toFullDto(Event event, Long views) {
        if (event == null) {
            return createEmptyEventFullDto();
        }

        try {
            String annotation = event.getAnnotation() != null ? event.getAnnotation() : "";

            CategoryDto category;
            try {
                category = event.getCategory() != null ?
                        CategoryMapper.toDto(event.getCategory()) :
                        new CategoryDto(0L, "");
            } catch (Exception e) {
                category = new CategoryDto(0L, "");
            }

            Long confirmedRequests = event.getConfirmedRequests() != null ?
                    event.getConfirmedRequests() : 0L;

            String createdOn;
            try {
                createdOn = event.getCreatedOn() != null ?
                        DateTimeUtils.format(event.getCreatedOn()) :
                        DateTimeUtils.format(LocalDateTime.now());
            } catch (Exception e) {
                createdOn = DateTimeUtils.format(LocalDateTime.now());
            }

            String description = event.getDescription() != null ? event.getDescription() : "";

            String eventDate;
            try {
                eventDate = event.getEventDate() != null ?
                        DateTimeUtils.format(event.getEventDate()) :
                        DateTimeUtils.format(LocalDateTime.now());
            } catch (Exception e) {
                eventDate = DateTimeUtils.format(LocalDateTime.now());
            }

            Long id = event.getId() != null ? event.getId() : 0L;

            UserShortDto initiator;
            try {
                initiator = event.getInitiator() != null ?
                        UserMapper.toShortDto(event.getInitiator()) :
                        new UserShortDto(0L, "");
            } catch (Exception e) {
                initiator = new UserShortDto(0L, "");
            }

            LocationDto location;
            try {
                location = event.getLocation() != null ?
                        LocationMapper.toDto(event.getLocation()) :
                        new LocationDto(0.0, 0.0);
            } catch (Exception e) {
                location = new LocationDto(0.0, 0.0);
            }

            Boolean paid = event.getPaid() != null ? event.getPaid() : false;
            Integer participantLimit = event.getParticipantLimit() != null ?
                    event.getParticipantLimit() : 0;

            // FIX: убрали пустой catch (Checkstyle). Здесь try/catch не нужен.
            String publishedOn = (event.getPublishedOn() != null)
                    ? DateTimeUtils.format(event.getPublishedOn())
                    : null;

            Boolean requestModeration = event.getRequestModeration() != null ?
                    event.getRequestModeration() : true;

            String state = event.getState() != null ?
                    event.getState().name() : "PENDING";

            String title = event.getTitle() != null ? event.getTitle() : "";

            Long resolvedViews = views != null ? views :
                    (event.getViews() != null ? event.getViews() : 0L);

            return new EventFullDto(
                    annotation,
                    category,
                    confirmedRequests,
                    createdOn,
                    description,
                    eventDate,
                    id,
                    initiator,
                    location,
                    paid,
                    participantLimit,
                    publishedOn,
                    requestModeration,
                    state,
                    title,
                    resolvedViews
            );
        } catch (Exception e) {
            return createEmptyEventFullDto();
        }
    }

    private static EventFullDto createEmptyEventFullDto() {
        return new EventFullDto(
                "",
                new CategoryDto(0L, ""),
                0L,
                DateTimeUtils.format(LocalDateTime.now()),
                "",
                DateTimeUtils.format(LocalDateTime.now()),
                0L,
                new UserShortDto(0L, ""),
                new LocationDto(0.0, 0.0),
                false,
                0,
                null,
                true,
                "PENDING",
                "",
                0L
        );
    }

    public static EventShortDto toShortDto(Event event, Long views) {
        if (event == null) {
            return createEmptyEventShortDto();
        }

        try {
            long resolvedViews = (views != null)
                    ? views
                    : (event.getViews() == null ? 0L : event.getViews());

            long confirmedRequests = (event.getConfirmedRequests() == null)
                    ? 0L
                    : event.getConfirmedRequests();

            return new EventShortDto(
                    event.getAnnotation() != null ? event.getAnnotation() : "",
                    event.getCategory() != null ? CategoryMapper.toDto(event.getCategory()) : new CategoryDto(0L, ""),
                    confirmedRequests,
                    event.getEventDate() != null ? DateTimeUtils.format(event.getEventDate()) : DateTimeUtils.format(LocalDateTime.now()),
                    event.getId() != null ? event.getId() : 0L,
                    event.getInitiator() != null ? UserMapper.toShortDto(event.getInitiator()) : new UserShortDto(0L, ""),
                    event.getPaid() != null ? event.getPaid() : false,
                    event.getTitle() != null ? event.getTitle() : "",
                    resolvedViews
            );
        } catch (Exception e) {
            return createEmptyEventShortDto();
        }
    }

    private static EventShortDto createEmptyEventShortDto() {
        return new EventShortDto(
                "",
                new CategoryDto(0L, ""),
                0L,
                DateTimeUtils.format(LocalDateTime.now()),
                0L,
                new UserShortDto(0L, ""),
                false,
                "",
                0L
        );
    }
}