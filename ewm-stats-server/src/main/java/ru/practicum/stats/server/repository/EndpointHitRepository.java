package ru.practicum.stats.server.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.model.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(h.id)) "
            + "FROM EndpointHit h "
            + "WHERE h.timestamp BETWEEN :start AND :end "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT(h.id) DESC")
    List<ViewStatsDto> findAllStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) "
            + "FROM EndpointHit h "
            + "WHERE h.timestamp BETWEEN :start AND :end "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> findAllStatsUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(h.id)) "
            + "FROM EndpointHit h "
            + "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT(h.id) DESC")
    List<ViewStatsDto> findStatsByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                       @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) "
            + "FROM EndpointHit h "
            + "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> findStatsByUrisUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);
}
