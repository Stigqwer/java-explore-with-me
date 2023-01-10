package ru.practicum.ewm.event;

import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;

public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto, Category category, User user, Location location) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(location)
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
        Category category = event.getCategory();
        if (category != null) {
            eventFullDto.setCategory(CategoryMapper.toCategoryDto(category));
        }
        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = EventShortDto.builder()
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
        Category category = event.getCategory();
        if (category != null) {
            eventShortDto.setCategory(CategoryMapper.toCategoryDto(category));
        }
        return eventShortDto;
    }
}
