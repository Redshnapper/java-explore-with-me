package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getAllUserRequests(Long userId);

    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);
}
