package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(NewEventDto eventDto, Long userId);

    List<EventFullDto> findAllByAdmin(Long[] users, String[] states, Long[] categories, String rangeStart,
                                      String rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(AdminUpdateEventRequest eventRequest, Long eventId);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    List<EventShortDto> findAllEventByPublicApi(HttpServletRequest request, String text, Long[] categories, Boolean paid,
                                                String rangeStart, String rangeEnd, boolean onlyAvailable, String sort,
                                                Integer from, Integer size);

    EventFullDto findEvent(Long id, HttpServletRequest request);

    List<EventShortDto> findAllEventByUser(Long userId, Integer from, Integer size);

    EventFullDto updateEventByUser(Long userId, UpdateEventRequest request);

    EventFullDto findEventByUser(Long userId, Long eventId);

    EventFullDto canceledEventByUser(Long userId, Long eventId);
}
