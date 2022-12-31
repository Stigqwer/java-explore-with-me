package ru.practicum.ewm.event;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                                    @PathVariable Long userId) {
        return eventService.createEvent(newEventDto, userId);
    }

    @GetMapping("admin/events")
    public List<EventFullDto> findAllEventByAdmin(
            @RequestParam(name = "users", required = false) Long[] users,
            @RequestParam(name = "states", required = false) String[] states,
            @RequestParam(name = "categories", required = false) Long[] categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("admin/events/{eventId}")
    public EventFullDto updateEventByAdmin(@RequestBody AdminUpdateEventRequest eventRequest,
                                           @PathVariable Long eventId){
        return eventService.updateEventByAdmin(eventRequest, eventId);
    }

    @PatchMapping("admin/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId){
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("admin/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId){
        return eventService.rejectEvent(eventId);
    }
}
