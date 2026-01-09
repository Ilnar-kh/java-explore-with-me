package ru.practicum.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.dto.LocationDto;
import ru.practicum.main.location.model.Location;
import ru.practicum.main.location.repository.LocationRepository;
import ru.practicum.main.mapper.LocationMapper;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Transactional
    public Location save(LocationDto dto) {
        return locationRepository.save(LocationMapper.toEntity(dto));
    }
}