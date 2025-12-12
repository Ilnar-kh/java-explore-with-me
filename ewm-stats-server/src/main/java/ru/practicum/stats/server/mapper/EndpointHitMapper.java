package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

public final class EndpointHitMapper {
    private EndpointHitMapper() {
    }

    public static EndpointHit toEntity(EndpointHitDto dto) {
        if (dto == null) {
            return null;
        }
        return new EndpointHit(
                dto.getId(),
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }

    public static EndpointHitDto toDto(EndpointHit entity) {
        if (entity == null) {
            return null;
        }
        return new EndpointHitDto(
                entity.getId(),
                entity.getApp(),
                entity.getUri(),
                entity.getIp(),
                entity.getTimestamp()
        );
    }
}
