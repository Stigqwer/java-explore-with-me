package ru.practicum.ewm.request;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId){
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> findAllRequestsByUser(@PathVariable Long userId){
        return requestService.findAllRequestsByUser(userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId){
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findAllRequestsByEvent(@PathVariable Long userId, @PathVariable Long eventId){
        return requestService.findAllRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable Long userId,
                                                  @PathVariable Long eventId, @PathVariable Long reqId){
        return requestService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable Long userId,
                                                  @PathVariable Long eventId, @PathVariable Long reqId){
        return requestService.rejectRequest(userId, eventId, reqId);
    }
}
