package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.stats.CreateStatsDto;
import ru.practicum.ewm.dto.stats.ViewDto;
import ru.practicum.ewm.enums.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.InvalidTimeException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.enums.EventState.CANCELED;
import static ru.practicum.ewm.enums.EventState.PENDING;
import static ru.practicum.ewm.enums.ParticipationRequestStatus.CONFIRMED;
import static ru.practicum.ewm.enums.ParticipationRequestStatus.REJECTED;
import static ru.practicum.ewm.enums.StateActionAdmin.PUBLISH_EVENT;
import static ru.practicum.ewm.enums.StateActionAdmin.REJECT_EVENT;
import static ru.practicum.ewm.enums.StateActionUser.CANCEL_REVIEW;
import static ru.practicum.ewm.enums.StateActionUser.SEND_TO_REVIEW;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RequestMapper requestMapper;
    private final StatsClient client;

    @Override
    public List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime start = Optional.ofNullable(rangeStart).orElse(LocalDateTime.now());
        LocalDateTime end = Optional.ofNullable(rangeEnd).orElse(start.plusDays(14));
        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, start, end, pageable);
        List<Long> eventIds = getEventIds(events);
        Map<String, Long> views = getEventsViews(events);
        Map<Long, Long> countRequestsByEventId = processParticipationRequests(eventIds);
        List<EventFullDto> fullDtoList = eventMapper.toFullDtoList(events);
        fullDtoList.forEach(eventFullDto -> {
            eventFullDto.setConfirmedRequests(countRequestsByEventId.getOrDefault(eventFullDto.getId(), 0L));
            eventFullDto.setViews(views.getOrDefault("/events/" + eventFullDto.getId(), 0L));
        });

        return fullDtoList;
    }

    @Override
    public List<EventShortDto> getAllPublished(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               boolean onlyAvailable, SortEvent sort, int from, int size,
                                               HttpServletRequest request) {
        saveStats(request);
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime start = Optional.ofNullable(rangeStart).orElse(LocalDateTime.now());
        LocalDateTime end = Optional.ofNullable(rangeEnd).orElse(start.plusDays(14));

        if (start.isAfter(end)) {
            throw new BadRequestException("Дата начала должна быть раньше окончания");
        }

        List<Event> events = eventRepository.getAllPublicByParams(text, categories, paid, start, end, pageable);

        if (onlyAvailable) {
            events = filterAvailableEvents(events);
        }

        List<Long> eventIds = getEventIds(events);
        Map<String, Long> views = getEventsViews(events);
        Map<Long, Long> countRequestsByEventId = processParticipationRequests(eventIds);
        List<EventShortDto> shortDtoList = eventMapper.toShortDtoList(events);
        shortDtoList.forEach(eventShortDto -> {
            eventShortDto.setConfirmedRequests(countRequestsByEventId.getOrDefault(eventShortDto.getId(), 0L));
            eventShortDto.setViews(views.getOrDefault("/events/" + eventShortDto.getId(), 0L));
        });

        switch (sort) {
            case EVENT_DATE:
                shortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
                break;
            case VIEWS:
                shortDtoList.sort(Comparator.comparing(EventShortDto::getViews).reversed());
                break;
        }
        return shortDtoList;
    }

    @Override
    public List<EventShortDto> getAllByInitiator(Long userId, Pageable pageable) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return eventMapper.toShortDtoList(events);
    }

    @Override
    public EventFullDto getByIdByInitiator(Long userId, Long eventId) {
        Event event = getEvent(eventId);
        checkEventUser(userId, eventId);
        return eventMapper.toFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(Long userId, Long eventId) {
        getEvent(eventId);
        getUser(userId);
        checkEventUser(userId, eventId);
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requestMapper.toDtoList(requests);
    }


    @Override
    public EventFullDto getPublishedById(Long eventId, HttpServletRequest request) {
        Event event = getEvent(eventId);
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException("Событие с id=" + eventId + " не опубликовано");
        }

        saveStats(request);
        EventFullDto fullDto = eventMapper.toFullDto(event);
        Map<String, Long> views = getEventsViews(Collections.singletonList(event));
        fullDto.setViews(views.getOrDefault("/events/" + fullDto.getId(), 0L));
        long limit = requestRepository.getCountByEventIdAndStatus(eventId, CONFIRMED);
        fullDto.setConfirmedRequests(limit);
        return fullDto;
    }

    @Override

    public EventFullDto createByInitiator(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidTimeException("Дата события указана не верно");
        }
        User initiator = getUser(userId);
        Category category = getCategory(newEventDto.getCategory());
        Location location = getOrCreateLocation(newEventDto.getLocation());
        Event event = eventMapper.toEvent(newEventDto, category, location);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);
        event.setPaid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false);
        event.setParticipantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0);
        event.setRequestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toFullDto(savedEvent);
    }

    @Override

    public EventFullDto updateByAdmin(UpdateEventAdminRequest adminRequest, Long eventId) {
        Event event = getEvent(eventId);
        checkEventStateConflictsForAdmin(adminRequest, event);
        updateEventParts(event, adminRequest.getCategory(), adminRequest.getLocation(),
                adminRequest.getAnnotation(), adminRequest.getTitle(), adminRequest.getDescription(),
                adminRequest.getParticipantLimit(), adminRequest.getEventDate(), adminRequest.getPaid(),
                adminRequest.getRequestModeration());

        StateActionAdmin stateAction = adminRequest.getStateAction();
        if (stateAction == PUBLISH_EVENT) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else {
            event.setState(stateAction == REJECT_EVENT ? EventState.CANCELED : event.getState());
        }
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toFullDto(updatedEvent);
    }

    @Override

    public EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventUserRequest userRequest) {
        Event event = getEvent(eventId);
        checkEventUser(userId, eventId);
        checkEventStateConflictsForInitiator(userRequest, event);
        updateEventParts(event, userRequest.getCategory(), userRequest.getLocation(),
                userRequest.getAnnotation(), userRequest.getTitle(), userRequest.getDescription(),
                userRequest.getParticipantLimit(), userRequest.getEventDate(), userRequest.getPaid(),
                userRequest.getRequestModeration());

        if (userRequest.getStateAction() != null) {
            Map<StateActionUser, EventState> stateMap = Map.of(
                    SEND_TO_REVIEW, PENDING,
                    CANCEL_REVIEW, CANCELED
            );

            EventState newState = stateMap.get(userRequest.getStateAction());

            if (newState != null) {
                event.setState(newState);
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toFullDto(updatedEvent);
    }

    private void updateEventParts(Event event, Long category, LocationDto location, String annotation,
                                  String title, String description, Integer participantLimit,
                                  LocalDateTime eventDate, Boolean paid, Boolean requestModeration) {

        if (location != null) event.setLocation(getOrCreateLocation(location));
        event.setCategory(category != null ? getCategory(category) : event.getCategory());
        event.setAnnotation(annotation != null ? annotation : event.getAnnotation());
        event.setTitle(title != null ? title : event.getTitle());
        event.setDescription(description != null ? description : event.getDescription());
        event.setParticipantLimit(participantLimit != null ? participantLimit : event.getParticipantLimit());
        event.setEventDate(eventDate != null ? eventDate : event.getEventDate());
        event.setPaid(paid != null ? paid : event.getPaid());
        event.setRequestModeration(requestModeration != null ? requestModeration : event.getRequestModeration());
    }

    @Override

    public EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest statusUpdateRequest) {
        Event event = getEvent(eventId);
        getUser(userId);
        long amountOfParticipants = requestRepository.getCountByEventIdAndStatus(eventId, CONFIRMED);
        long availableLimit = event.getParticipantLimit() - amountOfParticipants;
        if (availableLimit <= 0) {
            throw new ConflictException("Достигнут лимит участников");
        }

        List<Long> requestIds = statusUpdateRequest.getRequestIds();
        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(requestIds);

        EventRequestStatusUpdateResult statusUpdateResult = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
            return statusUpdateResult;
        }

        for (ParticipationRequest request : requests) {
            if (!eventId.equals(request.getEvent().getId())) {
                throw new NotFoundException("Событие с id=" + request.getEvent().getId() + " не найдено");
            }
            if (!ParticipationRequestStatus.PENDING.equals(request.getStatus())) {
                throw new BadRequestException("Статус запроса=" + request.getStatus() + " указан неправильно");
            }
            if (availableLimit <= 0) {
                request.setStatus(REJECTED);
                statusUpdateResult.getRejectedRequests().add(requestMapper.toDto(request));
            }
            switch (statusUpdateRequest.getStatus()) {
                case CONFIRMED:
                    request.setStatus(CONFIRMED);
                    statusUpdateResult.getConfirmedRequests().add(requestMapper.toDto(request));
                    availableLimit--;
                    break;
                case REJECTED:
                    request.setStatus(REJECTED);
                    statusUpdateResult.getRejectedRequests().add(requestMapper.toDto(request));
                    break;
            }
        }
        requestRepository.saveAll(requests);
        return statusUpdateResult;
    }

    private Location getOrCreateLocation(LocationDto locationDto) {
        float lat = locationDto.getLat();
        float lon = locationDto.getLon();
        Location location = locationRepository.findByLatAndLon(lat, lon);
        location = (location == null) ? locationRepository.save(locationMapper.toLocation(locationDto)) : location;
        return location;
    }

    private void checkEventStateConflictsForAdmin(UpdateEventAdminRequest adminRequest, Event event) {

        if (adminRequest.getEventDate() != null && adminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidTimeException("Дата события должна быть не раньше, чем: " + LocalDateTime.now().plusHours(1));
        }

        if (adminRequest.getStateAction() != null) {

            if (event.getState().equals(EventState.PUBLISHED) && adminRequest.getStateAction().equals(REJECT_EVENT)) {
                throw new ConflictException("Событие со статусом: " + event.getState() + " нельзя отклонить");
            }

            if (!event.getState().equals(PENDING) && adminRequest.getStateAction().equals(PUBLISH_EVENT)) {
                throw new ConflictException("Событие со статусом: " + event.getState() + " нельзя опубликовать");
            }
        }
    }

    private void checkEventStateConflictsForInitiator(UpdateEventUserRequest userRequest, Event event) {

        if (userRequest.getEventDate() != null && userRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidTimeException("Дата события должна быть не раньше, чем: " + LocalDateTime.now().plusHours(2));
        }

        if (!(event.getState().equals(PENDING) || event.getState().equals(CANCELED))) {
            throw new ConflictException("Данное событие нельзя изменить");
        }
    }

    private void saveStats(HttpServletRequest request) {
        String app = "main-service";
        client.createHit(CreateStatsDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .date(LocalDateTime.now())
                .build());
    }

    private Map<String, Long> getEventsViews(List<Event> events) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        List<String> eventUris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        LocalDateTime minPublishedDate = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (minPublishedDate == null) {
            return Collections.emptyMap();
        }

        start = minPublishedDate;
        List<ViewDto> views = client.getViews(start, end, eventUris);
        return views.stream()
                .collect(Collectors.toMap(ViewDto::getUri, ViewDto::getHits));
    }

    private void checkEventUser(Long userId, Long eventId) {
        Event event = getEvent(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new NotFoundException("У события с id=" + eventId + " другой огранизатор");
        }
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не найдена"));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + id + " не найдено"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    private List<Long> getEventIds(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
    }

    private Map<Long, Long> processParticipationRequests(List<Long> eventIds) {
        List<ParticipationRequest> allRequests = requestRepository.findAllByEventIdInAndStatus(eventIds, CONFIRMED);
        return allRequests.stream()
                .collect(Collectors.groupingBy(r -> r.getEvent().getId(), Collectors.counting()));
    }

    private List<Event> filterAvailableEvents(List<Event> events) {
        return events.stream()
                .filter(event -> event.getParticipantLimit().equals(0)
                        || (requestRepository.getCountByEventIdAndStatus(event.getId(), CONFIRMED)
                        < event.getParticipantLimit()))
                .collect(Collectors.toList());
    }
}
