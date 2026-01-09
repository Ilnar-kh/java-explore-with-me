package ru.practicum.main.mapper;

import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.util.DateTimeUtils;

public class ParticipationRequestMapper {

    private ParticipationRequestMapper() {
    }

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                DateTimeUtils.format(request.getCreated()),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus().name()
        );
    }
}