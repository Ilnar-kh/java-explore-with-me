package ru.practicum.main.dto;

import jakarta.validation.constraints.NotNull;

public class LocationDto {

    @NotNull
    private Double lat;

    @NotNull
    private Double lon;

    public LocationDto() {
    }

    public LocationDto(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}