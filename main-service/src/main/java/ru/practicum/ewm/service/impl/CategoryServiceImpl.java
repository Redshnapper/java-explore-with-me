package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = mapper.toCategory(categoryDto);
        Category createdCategory = repository.save(category);
        return mapper.toDto(createdCategory);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, Long id) {
        Category saved = getCategory(id);
        saved.setName(categoryDto.getName());
        Category updatedCategory = repository.save(saved);
        return mapper.toDto(updatedCategory);
    }

    @Override
    public void deleteById(Long id) {
        getCategory(id);
        repository.deleteById(id);
    }

    @Override

    public CategoryDto getById(Long catId) {
        Category category = getCategory(catId);
        return mapper.toDto(category);
    }

    @Override

    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = repository.findAll(pageable).getContent();
        return mapper.toDtoList(categories);
    }

    private Category getCategory(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Категория с id = " + id + " не найдена"));
    }
}
