package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.LocationDto;
import ru.practicum.main.location.model.Location;

@UtilityClass
public class LocationMapper {

    public Location toEntity(LocationDto dto) {
        Location location = new Location();
        location.setLat(dto.getLat().floatValue());
        location.setLon(dto.getLon().floatValue());
        return location;
    }

    public LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat().doubleValue())
                .lon(location.getLon().doubleValue())
                .build();
    }
}
