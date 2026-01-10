package ru.practicum.main.dto;

public class CommentDto {

    private final Long id;
    private final String text;
    private final UserShortDto author;
    private final Long eventId;
    private final String createdOn;
    private final String updatedOn;

    public CommentDto(Long id, String text, UserShortDto author, Long eventId, String createdOn, String updatedOn) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.eventId = eventId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public UserShortDto getAuthor() {
        return author;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }
}
