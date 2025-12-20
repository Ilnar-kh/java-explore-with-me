package ru.practicum.main.mapper;

import ru.practicum.main.dto.LocationDto;
import ru.practicum.main.location.model.Location;

public class LocationMapper {

    private LocationMapper() {
    }

    public static Location toEntity(LocationDto dto) {
        if (dto == null) {
            return null;
        }

        Location location = new Location();
        location.setLat((double) dto.getLat().floatValue());
        location.setLon((double) dto.getLon().floatValue());
        return location;
    }

    public static LocationDto toDto(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationDto(
                location.getLat().doubleValue(),
                location.getLon().doubleValue()
        );
    }
}