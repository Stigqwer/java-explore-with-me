package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryNotFoundException;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.error.ValidationException;
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserNotFoundException;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public EventFullDto createEvent(NewEventDto eventDto, Long userId) {
        LocalDateTime minimalDateEvent = LocalDateTime.now().plusHours(2);
        LocalDateTime eventDate = eventDto.getEventDate();
        if (minimalDateEvent.isAfter(eventDate)) {
            throw new ValidationException("До времени начала события меньше двух часов");
        }
        Category category = null;
        if (eventDto.getCategory() != null) {
            Optional<Category> optionalCategory = categoryRepository.findById(eventDto.getCategory());
            if (optionalCategory.isEmpty()) {
                throw new CategoryNotFoundException(String.format("Категория с id %d не найдена",
                        eventDto.getCategory()));
            }
            category = optionalCategory.get();
        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        Location location = eventDto.getLocation();
        Optional<Location> optionalLocation = locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        if (optionalLocation.isEmpty()) {
            locationRepository.save(location);
        }
        Event event = EventMapper.toEvent(eventDto, category, user, location);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> findAllByAdmin(Long[] users, String[] states, Long[] categories,
                                             String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<Event> events = eventRepository.findAll(PageRequest.of(((from) / size), size)).toList();
        if (users != null) {
            List<Long> usersId = Arrays.asList(users);
            events = events.stream()
                    .filter(event -> usersId.contains(event.getInitiator().getId())).collect(Collectors.toList());
        }
        if (states != null) {
            List<String> state = Arrays.asList(states);
            events = events.stream()
                    .filter(event -> state.contains(event.getState().toString())).collect(Collectors.toList());
        }
        if (categories != null) {
            List<Long> categoriesId = Arrays.asList(categories);
            events = events.stream()
                    .filter(event -> categoriesId.contains(event.getCategory().getId())).collect(Collectors.toList());
        }
        if (rangeStart != null) {
            LocalDateTime dateStart =
                    LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            events = events.stream()
                    .filter(event -> dateStart.isBefore(event.getEventDate())).collect(Collectors.toList());
        }
        if (rangeEnd != null) {
            LocalDateTime dateEnd =
                    LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            events = events.stream()
                    .filter(event -> dateEnd.isAfter(event.getEventDate())).collect(Collectors.toList());
        }
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(AdminUpdateEventRequest eventRequest, Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        event.setAnnotation(eventRequest.getAnnotation());
        if (eventRequest.getCategory() != null) {
            Optional<Category> optionalCategory = categoryRepository.findById(eventRequest.getCategory());
            if (optionalCategory.isEmpty()) {
                throw new CategoryNotFoundException
                        (String.format("Категории с id %d не найдено", eventRequest.getCategory()));
            } else{
                Category category = optionalCategory.get();
                event.setCategory(category);
            }
        } else {
            event.setCategory(null);
        }
        event.setDescription(eventRequest.getDescription());
        event.setEventDate(eventRequest.getEventDate());
        Location location = eventRequest.getLocation();
        Optional<Location> optionalLocation = locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        if(optionalLocation.isEmpty()){
            locationRepository.save(location);
        }
        event.setLocation(location);
        event.setPaid(eventRequest.isPaid());
        event.setParticipantLimit(eventRequest.getParticipantLimit());
        event.setRequestModeration(eventRequest.isRequestModeration());
        event.setTitle(eventRequest.getTitle());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if(optionalEvent.isEmpty()){
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        LocalDateTime dateNow = LocalDateTime.now();
        LocalDateTime minimalDateEvent = dateNow.plusHours(1);
        if(minimalDateEvent.isAfter(event.getEventDate())){
            throw new ValidationException("До времени начала события меньше одного часа");
        }
        if(event.getState() != State.PENDING){
            throw new ValidationException("Событие не в состоянии ожидания публикации");
        }
        event.setPublishedOn(dateNow);
        event.setState(State.PUBLISHED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if(optionalEvent.isEmpty()){
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        if(event.getState() == State.PUBLISHED){
            throw new ValidationException("Событие уже опубликовано");
        }
        event.setState(State.CANCELED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }
}
