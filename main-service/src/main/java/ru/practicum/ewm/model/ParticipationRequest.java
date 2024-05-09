package ru.practicum.ewm.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.enums.ParticipationRequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "participation_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ParticipationRequestStatus status;

    @Column(name = "created_date")
    LocalDateTime created;
}
