package ru.practicum.main.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

@Service
public class StatsService {
    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    private static final LocalDateTime START = LocalDateTime.of(2000, 1, 1, 0, 0);

    private final StatsClient statsClient;
    private final String appName;

    public StatsService(StatsClient statsClient,
                        @Value("${spring.application.name}") String appName) {
        this.statsClient = statsClient;
        this.appName = appName;
    }

    public void hit(HttpServletRequest request) {
        EndpointHitDto hitDto = new EndpointHitDto(null, appName, request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());
        try {
            statsClient.hit(hitDto);
        } catch (Exception ex) {
            log.warn("Failed to send stats hit: {}", ex.getMessage());
        }
    }

    public Map<String, Long> getViews(List<String> uris) {
        if (uris == null || uris.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            List<ViewStatsDto> stats = statsClient.getStats(START, LocalDateTime.now(), uris, false);
            Map<String, Long> result = new HashMap<>();
            for (ViewStatsDto stat : stats) {
                result.put(stat.getUri(), stat.getHits());
            }
            return result;
        } catch (Exception ex) {
            log.warn("Failed to fetch stats: {}", ex.getMessage());
            return Collections.emptyMap();
        }
    }
}
