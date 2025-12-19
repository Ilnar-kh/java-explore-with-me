package ru.practicum.main.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.dto.CompilationDto;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toDto(Compilation compilation, Map<Long, Long> eventViews) {
        List<ru.practicum.main.dto.EventShortDto> events = compilation.getEvents()
                .stream()
                .map(event -> EventMapper.toShortDto(event, resolveViews(event, eventViews)))
                .collect(Collectors.toList());

        return CompilationDto.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    private Long resolveViews(Event event, Map<Long, Long> viewsMap) {
        if (viewsMap == null) {
            return null;
        }
        return viewsMap.getOrDefault(event.getId(), event.getViews());
    }
}
