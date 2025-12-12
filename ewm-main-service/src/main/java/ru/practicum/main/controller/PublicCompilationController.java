package ru.practicum.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.CompilationDto;

import java.util.List;

@RestController
@RequestMapping("/compilations")
public class PublicCompilationController {

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
