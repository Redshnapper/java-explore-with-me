package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.model.Category;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    List<CategoryDto> toDtoList(List<Category> categories);
}
