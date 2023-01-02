package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(NewEventDto eventDto, Long userId);

    List<EventFullDto> findAllByAdmin(Long[] users, String[] states, Long[] categories, String rangeStart,
                                      String rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(AdminUpdateEventRequest eventRequest, Long eventId);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    List<EventShortDto> findAllEventByPublicApi(String text, Long[] categories, Boolean paid, String rangeStart,
                                                String rangeEnd, boolean onlyAvailable, String sort, Integer from,
                                                Integer size);
}
