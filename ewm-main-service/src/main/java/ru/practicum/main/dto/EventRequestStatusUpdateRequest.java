package ru.practicum.main.dto;

import java.util.List;

public class EventRequestStatusUpdateRequest {

    private final List<Long> requestIds;
    private final String status;

    public EventRequestStatusUpdateRequest(List<Long> requestIds, String status) {
        this.requestIds = requestIds;
        this.status = status;
    }

    public List<Long> getRequestIds() {
        return requestIds;
    }

    public String getStatus() {
        return status;
    }
}