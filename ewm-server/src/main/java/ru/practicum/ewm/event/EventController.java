package ru.practicum.ewm.event;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;

import javax.servlet.http.HttpServletRequest;
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
                                           @PathVariable Long eventId) {
        return eventService.updateEventByAdmin(eventRequest, eventId);
    }

    @PatchMapping("admin/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("admin/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        return eventService.rejectEvent(eventId);
    }

    @GetMapping("/events")
    public List<EventShortDto> findAllEventByPublicApi(
            HttpServletRequest request,
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) Long[] categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
            @RequestParam(name = "sort") String sort,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.findAllEventByPublicApi(request, text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{id}")
    public EventFullDto findEvent(@PathVariable Long id, HttpServletRequest request) {
        return eventService.findEvent(id, request);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> findAllEventByUser(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size){
        return eventService.findAllEventByUser(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                         @RequestBody @Valid UpdateEventRequest request){
        return eventService.updateEventByUser(userId, request);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto findEventByUser(@PathVariable Long userId, @PathVariable Long eventId){
        return eventService.findEventByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto canceledEventByUser(@PathVariable Long userId, @PathVariable Long eventId){
        return eventService.canceledEventByUser(userId, eventId);
    }
}
