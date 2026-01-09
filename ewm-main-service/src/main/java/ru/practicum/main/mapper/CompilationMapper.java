package ru.practicum.main.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.event.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {

    public static CompilationDto toDto(Compilation compilation, Map<Long, Long> eventViews) {

        List<EventShortDto> events = compilation.getEvents()
                .stream()
                .map(event -> EventMapper.toShortDto(event, resolveViews(event, eventViews)))
                .collect(Collectors.toList());

        return new CompilationDto(
                events,
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }

    private static Long resolveViews(Event event, Map<Long, Long> viewsMap) {
        if (viewsMap == null) {
            return event.getViews();
        }
        return viewsMap.getOrDefault(event.getId(), event.getViews());
    }
}