package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(@PathVariable long userId,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventService.getAllByInitiator(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId,
                               @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Создание события инициатором: {}", newEventDto);
        return eventService.createByInitiator(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable Long userId,
                                @PathVariable Long eventId) {
        return eventService.getByIdByInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest userRequest) {
        log.debug("Обновление события инициатором: {}", userRequest);
        return eventService.updateByInitiator(userId, eventId, userRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return eventService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateStatusOfRequests(@PathVariable Long userId,
                                                                 @PathVariable Long eventId,
                                                                 @Valid @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.debug("Обновление статусов запросов на участие: {}", statusUpdateRequest);
        return eventService.updateRequestsStatus(userId, eventId, statusUpdateRequest);
    }
}
