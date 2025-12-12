package ru.practicum.main.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}
