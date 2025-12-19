package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;

@UtilityClass
public class CategoryMapper {

    public Category toEntity(NewCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
