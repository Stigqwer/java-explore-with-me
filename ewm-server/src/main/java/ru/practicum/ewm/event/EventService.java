package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;

public interface EventService {
    EventFullDto createEvent(NewEventDto eventDto, Long userId);
}
