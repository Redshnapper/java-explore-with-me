package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long catId);

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto, Long id);

    void deleteById(Long catId);
}
