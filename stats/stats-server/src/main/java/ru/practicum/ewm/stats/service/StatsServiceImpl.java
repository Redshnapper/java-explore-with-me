package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.stats.CreateStatsDto;
import ru.practicum.ewm.dto.stats.ViewDto;
import ru.practicum.ewm.stats.mapper.StatsMapper;
import ru.practicum.ewm.stats.model.EndpointHit;
import ru.practicum.ewm.stats.repository.StatsServerRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsServerRepository repository;
    private final StatsMapper mapper;


    @Override
    public List<ViewDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique.equals(true)) {
            return repository.findStatsByDatesUniqueIp(start, end, uris);
        }
        return repository.findStatsByDates(start, end, uris);
    }

    @Override
    public CreateStatsDto create(CreateStatsDto createDto) {
        EndpointHit stats = mapper.toModel(createDto);
        return mapper.toDto(repository.save(stats));
    }


}