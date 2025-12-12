package ru.practicum.main.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.dto.UpdateCompilationRequest;

@RestController
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    @PostMapping
    public ResponseEntity<CompilationDto> saveCompilation(@Valid @RequestBody NewCompilationDto dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @Valid @RequestBody UpdateCompilationRequest dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
