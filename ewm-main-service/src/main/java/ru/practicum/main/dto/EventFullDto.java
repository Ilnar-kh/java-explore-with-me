package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EventFullDto {

    @NotBlank
    private final String annotation;

    @NotNull
    private final CategoryDto category;

    @NotNull
    private final Long confirmedRequests;

    @NotBlank
    private final String createdOn;

    @NotBlank
    private final String description;

    @NotBlank
    private final String eventDate;

    @NotNull
    private final Long id;

    @NotNull
    private final UserShortDto initiator;

    @NotNull
    private final LocationDto location;

    @NotNull
    private final Boolean paid;

    @NotNull
    private final Integer participantLimit;

    private final String publishedOn;

    @NotNull
    private final Boolean requestModeration;

    @NotBlank
    private final String state;

    @NotBlank
    private final String title;

    private final Long views;

    public EventFullDto(
            String annotation,
            CategoryDto category,
            Long confirmedRequests,
            String createdOn,
            String description,
            String eventDate,
            Long id,
            UserShortDto initiator,
            LocationDto location,
            Boolean paid,
            Integer participantLimit,
            String publishedOn,
            Boolean requestModeration,
            String state,
            String title,
            Long views
    ) {
        this.annotation = annotation;
        this.category = category;
        this.confirmedRequests = confirmedRequests;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.views = views;
    }

    public String getAnnotation() {
        return annotation;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public Long getConfirmedRequests() {
        return confirmedRequests;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getDescription() {
        return description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public Long getId() {
        return id;
    }

    public UserShortDto getInitiator() {
        return initiator;
    }

    public LocationDto getLocation() {
        return location;
    }

    public Boolean getPaid() {
        return paid;
    }

    public Integer getParticipantLimit() {
        return participantLimit;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public Boolean getRequestModeration() {
        return requestModeration;
    }

    public String getState() {
        return state;
    }

    public String getTitle() {
        return title;
    }

    public Long getViews() {
        return views;
    }
}