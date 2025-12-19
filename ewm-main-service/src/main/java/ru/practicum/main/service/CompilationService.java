package ru.practicum.main.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.dto.UpdateCompilationRequest;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatsService statsService;

    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null && dto.getPinned());
        compilation.setEvents(fetchEvents(dto.getEvents()));

        Compilation saved = compilationRepository.save(compilation);
        return CompilationMapper.toDto(saved, resolveViews(saved));
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getEvents() != null) {
            compilation.setEvents(fetchEvents(dto.getEvents()));
        }
        Compilation saved = compilationRepository.save(compilation);
        return CompilationMapper.toDto(saved, resolveViews(saved));
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest).getContent();
        }
        return compilations.stream()
                .map(compilation -> CompilationMapper.toDto(compilation, resolveViews(compilation)))
                .toList();
    }

    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        return CompilationMapper.toDto(compilation, resolveViews(compilation));
    }

    private Set<Event> fetchEvents(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        List<Event> events = eventRepository.findAllById(ids);
        if (events.size() != ids.size()) {
            throw new NotFoundException("Some events were not found");
        }
        return new HashSet<>(events);
    }

    private Map<Long, Long> resolveViews(Compilation compilation) {
        if (compilation.getEvents().isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> uris = compilation.getEvents().stream()
                .map(event -> "/events/" + event.getId())
                .toList();
        Map<String, Long> stats = statsService.getViews(uris);
        return compilation.getEvents().stream()
                .collect(Collectors.toMap(Event::getId,
                        event -> stats.getOrDefault("/events/" + event.getId(), event.getViews())));
    }
}
