package ru.practicum.ewm.stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.stats.CreateStatsDto;
import ru.practicum.ewm.stats.model.EndpointHit;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface StatsMapper {
    EndpointHit toModel(CreateStatsDto dto);

    CreateStatsDto toDto(EndpointHit hit);

}
