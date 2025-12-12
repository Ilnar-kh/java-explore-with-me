package ru.practicum.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.CategoryDto;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class PublicCategoryController {

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
