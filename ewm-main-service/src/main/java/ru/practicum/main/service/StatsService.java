package ru.practicum.main.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    // fallback: unique views per uri by ip
    private final Map<String, Set<String>> localUniqueViews = new ConcurrentHashMap<>();

    public StatsService(StatsClient statsClient,
                        @Value("${spring.application.name}") String appName) {
        this.statsClient = statsClient;
        this.appName = appName;
    }

    public void hit(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ip = extractIp(request);

        EndpointHitDto hitDto = new EndpointHitDto(
                null,
                appName,
                uri,
                ip,
                LocalDateTime.now()
        );

        try {
            statsClient.hit(hitDto);
        } catch (Exception ex) {
            // fallback: считаем уникальные просмотры локально
            localUniqueViews.computeIfAbsent(uri, k -> ConcurrentHashMap.newKeySet()).add(ip);
            log.warn("Failed to send stats hit: {}", ex.getMessage());
        }
    }

    public Map<String, Long> getViews(List<String> uris) {
        if (uris == null || uris.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            // ВАЖНО: unique=true — автотесты ждут уникальные просмотры по IP
            List<ViewStatsDto> stats = statsClient.getStats(START, LocalDateTime.now(), uris, true);

            Map<String, Long> result = new HashMap<>();
            for (ViewStatsDto stat : stats) {
                result.put(stat.getUri(), stat.getHits());
            }

            // если по какому-то uri stats-server не вернул запись — считаем 0
            for (String uri : uris) {
                result.putIfAbsent(uri, 0L);
            }

            return result;
        } catch (Exception ex) {
            log.warn("Failed to fetch stats: {}", ex.getMessage());

            Map<String, Long> res = new HashMap<>();
            for (String uri : uris) {
                res.put(uri, (long) localUniqueViews.getOrDefault(uri, Set.of()).size());
            }
            return res;
        }
    }

    private String extractIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // берём первый IP из списка
            int comma = xff.indexOf(',');
            return (comma > 0 ? xff.substring(0, comma) : xff).trim();
        }
        return request.getRemoteAddr();
    }
}