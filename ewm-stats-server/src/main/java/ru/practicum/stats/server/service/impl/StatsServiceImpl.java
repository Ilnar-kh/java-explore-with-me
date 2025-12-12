package ru.practicum.stats.server.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.exception.BadRequestException;
import ru.practicum.stats.server.mapper.EndpointHitMapper;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.repository.EndpointHitRepository;
import ru.practicum.stats.server.service.StatsService;

@Service
public class StatsServiceImpl implements StatsService {
    private final EndpointHitRepository endpointHitRepository;

    public StatsServiceImpl(EndpointHitRepository endpointHitRepository) {
        this.endpointHitRepository = endpointHitRepository;
    }

    @Override
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit hit = EndpointHitMapper.toEntity(endpointHitDto);
        EndpointHit saved = endpointHitRepository.save(hit);
        return EndpointHitMapper.toDto(saved);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Start date must be before end date");
        }
        boolean filterUris = !CollectionUtils.isEmpty(uris);
        if (unique) {
            return filterUris
                    ? endpointHitRepository.findStatsByUrisUnique(start, end, uris)
                    : endpointHitRepository.findAllStatsUnique(start, end);
        }
        return filterUris
                ? endpointHitRepository.findStatsByUris(start, end, uris)
                : endpointHitRepository.findAllStats(start, end);
    }
}
