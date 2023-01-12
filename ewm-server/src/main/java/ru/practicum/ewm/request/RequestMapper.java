package ru.practicum.ewm.request;

import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;

public class RequestMapper {
    public static Request toRequest(Event event, User user) {
        return Request.builder()
                .event(event)
                .user(user)
                .status(Status.PENDING)
                .build();
    }

    public static ParticipationRequestDto toRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getUser().getId())
                .status(request.getStatus())
                .build();
    }
}
