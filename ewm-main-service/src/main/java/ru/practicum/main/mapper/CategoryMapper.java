package ru.practicum.main.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {

    public static Category toEntity(NewCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}