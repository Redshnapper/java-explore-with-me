package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper mapper;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Long> eventIds = newCompilationDto.getEvents();
        List<Event> events = (eventIds != null && !eventIds.isEmpty()) ?
                eventRepository.findAllById(eventIds) : new ArrayList<>();
        newCompilationDto.setPinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false);
        Compilation compilation = mapper.toCompilation(newCompilationDto, events);
        Compilation savedCompilation = compilationRepository.save(compilation);
        return mapper.toDto(savedCompilation);
    }

    @Override
    public CompilationDto update(Long id, UpdateCompilationRequest compilationRequest) {
        Compilation compilation = getCompilation(id);
        List<Long> eventIds = compilationRequest.getEvents();
        compilation.setEvents(eventIds != null && !eventIds.isEmpty() ?
                eventRepository.findAllById(eventIds) : compilation.getEvents());

        String title = compilationRequest.getTitle();
        compilation.setTitle(title != null ? title : compilation.getTitle());

        Boolean pinned = compilationRequest.getPinned();
        compilation.setPinned(pinned != null ? pinned : compilation.getPinned());

        Compilation updatedCompilation = compilationRepository.save(compilation);
        return mapper.toDto(updatedCompilation);
    }


    @Override
    public void deleteById(Long id) {
        getCompilation(id);
        compilationRepository.deleteById(id);
    }

    @Override

    public CompilationDto getById(Long id) {
        Compilation compilation = getCompilation(id);
        return mapper.toDto(compilation);
    }

    @Override

    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllPinned(pinned, pageable);
        return mapper.toDtoList(compilations);
    }

    private Compilation getCompilation(Long id) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Подборка с id= " + id + " не найдена"));
    }
}
