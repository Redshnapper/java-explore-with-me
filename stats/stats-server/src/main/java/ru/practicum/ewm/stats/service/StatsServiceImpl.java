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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsServerRepository repository;
    private final StatsMapper mapper;


    @Override
    public List<ViewDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris != null && !uris.isEmpty()) {
            uris = uris.stream()
                    .map(uri -> uri.replace("[", "").replace("]", ""))
                    .collect(Collectors.toList());
        }

        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return repository.findStatsByDatesUniqueIpWithoutUris(start, end);
            }
            return repository.findStatsByDatesUniqueIp(start, end, uris);
        } else {
            if (uris == null || uris.isEmpty()) {
                return repository.findStatsByDatesWithoutUris(start, end);
            }
            return repository.findStatsByDates(start, end, uris);
        }
    }


    @Override
    public CreateStatsDto create(CreateStatsDto createDto) {
        EndpointHit stats = mapper.toModel(createDto);
        if (stats.getDate() == null) {
            stats.setDate(LocalDateTime.now());
        }
        return mapper.toDto(repository.save(stats));
    }
}
