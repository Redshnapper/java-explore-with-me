package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.SortEvent;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getAllByInitiator(Long userId, Pageable pageable);

    EventFullDto getByIdByInitiator(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByEventId(Long userId, Long eventId);

    List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);


    List<EventShortDto> getAllPublished(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                        SortEvent sort, int from, int size, HttpServletRequest request);

    EventFullDto updateByAdmin(UpdateEventAdminRequest adminRequest, Long eventId);

    EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventUserRequest userRequest);

    EventFullDto getPublishedById(Long eventId, HttpServletRequest request);

    EventFullDto createByInitiator(Long userId, NewEventDto newEventDto);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest statusUpdateRequest);
}
