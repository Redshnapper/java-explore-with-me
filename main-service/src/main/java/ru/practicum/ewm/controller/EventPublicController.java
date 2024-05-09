package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.enums.SortEvent;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(@RequestParam(defaultValue = "") String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                      @RequestParam(defaultValue = "VIEWS") SortEvent sort,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size,
                                      HttpServletRequest request) {
        log.info("Получение всех опубликованных событий: text= {}, paid= {}, rangeStart= {}, rangeEnd= {}, " +
                        "onlyAvailable= {}, sort= {}, from= {}, size= {}",
                text, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getAllPublished(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable Long id,
                                HttpServletRequest request) {
        log.info("Получение опубликованного события с id= {}", id);
        return eventService.getPublishedById(id, request);
    }
}
