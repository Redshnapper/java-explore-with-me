package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.ParticipationRequestStatus;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User requester = getUser(userId);
        Event event = getEvent(eventId);

        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Организатор не может добавить запрос на участие");
        }
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        Long limit = requestRepository.getCountByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);

        if (event.getParticipantLimit() > 0
                && limit >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит участников");
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setRequester(requester);
        participationRequest.setEvent(event);
        participationRequest.setStatus((event.getParticipantLimit() != 0 && event.getRequestModeration())
                ? ParticipationRequestStatus.PENDING
                : ParticipationRequestStatus.CONFIRMED);

        ParticipationRequest savedRequest = requestRepository.save(participationRequest);
        return requestMapper.toDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllUserRequests(Long userId) {
        getUser(userId);
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requestMapper.toDtoList(requests);
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        ParticipationRequest request = getRequest(requestId);
        getUser(userId);

        if (!userId.equals(request.getRequester().getId())) {
            throw new NotFoundException("Запрос участия для пользователя c id= "
                    + userId + " для данного события не найден");
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);
        ParticipationRequest canceled = requestRepository.save(request);
        return requestMapper.toDto(canceled);
    }

    private ParticipationRequest getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на участие с id=" + requestId + " не найден"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }
}
