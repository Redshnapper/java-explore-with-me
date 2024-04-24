package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.stats.CreateStatsDto;
import ru.practicum.ewm.dto.stats.ViewDto;
import ru.practicum.ewm.stats.service.StatsService;
import ru.practicum.ewm.stats.util.Constants;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @GetMapping("/stats")
    public List<ViewDto> get(@RequestParam @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime start,
                             @RequestParam @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime end,
                             @RequestParam List<String> uris,
                             @RequestParam(defaultValue = "false") Boolean unique) {
        return service.get(start, end, uris, unique);

    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateStatsDto create(@RequestBody @Valid CreateStatsDto createDto) {
        return service.create(createDto);
    }
}
