package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.LocationDto;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location){
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());
        return locationDto;
    }
}
