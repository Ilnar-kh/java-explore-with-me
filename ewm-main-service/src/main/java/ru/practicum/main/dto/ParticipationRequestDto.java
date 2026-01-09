package ru.practicum.main.dto;

public class ParticipationRequestDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;

    public ParticipationRequestDto() {
    }

    public ParticipationRequestDto(String created, Long event, Long id, Long requester, String status) {
        this.created = created;
        this.event = event;
        this.id = id;
        this.requester = requester;
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequester() {
        return requester;
    }

    public void setRequester(Long requester) {
        this.requester = requester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}