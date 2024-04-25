package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.dto.stats.CreateStatsDto;
import ru.practicum.ewm.dto.stats.ViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    CreateStatsDto create(CreateStatsDto createDto);

    List<ViewDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}

