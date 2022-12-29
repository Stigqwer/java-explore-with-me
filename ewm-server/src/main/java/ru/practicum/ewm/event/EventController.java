package ru.practicum.ewm.event;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;

import javax.validation.Valid;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                                    @PathVariable Long userId){
        return eventService.createEvent(newEventDto, userId);
    }
}
