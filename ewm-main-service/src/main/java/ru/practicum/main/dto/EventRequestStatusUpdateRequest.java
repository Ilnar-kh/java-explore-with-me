package ru.practicum.main.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EventRequestStatusUpdateRequest {
    List<Long> requestIds;
    String status;
}
