package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.enums.ParticipationRequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query("SELECT COUNT(r) FROM ParticipationRequest r WHERE r.event.id = :eventId AND r.status = :status")
    Long getCountByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") ParticipationRequestStatus status);

    List<ParticipationRequest> findAllByEventIdInAndStatus(List<Long> eventIds, ParticipationRequestStatus status);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);
}
