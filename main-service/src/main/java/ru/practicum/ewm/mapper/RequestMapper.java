package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "requester", source = "participationRequest.requester.id")
    @Mapping(target = "event", source = "participationRequest.event.id")
    ParticipationRequestDto toDto(ParticipationRequest participationRequest);

    List<ParticipationRequestDto> toDtoList(List<ParticipationRequest> participationRequests);
}
