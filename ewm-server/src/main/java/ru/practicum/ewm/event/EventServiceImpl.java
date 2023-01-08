package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryNotFoundException;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.error.ValidationException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.stats.EndpointHit;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserNotFoundException;
import ru.practicum.ewm.user.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventClient eventClient;

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
        List<Event> events = eventRepository.findAll();
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
        events = filterEventByDate(rangeStart, rangeEnd, events);
        events = events.stream().skip(from).limit(size).collect(Collectors.toList());
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    private List<Event> filterEventByDate(String rangeStart, String rangeEnd, List<Event> events) {
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
        return events;
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
            } else {
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
        if (optionalLocation.isEmpty()) {
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
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        LocalDateTime dateNow = LocalDateTime.now();
        LocalDateTime minimalDateEvent = dateNow.plusHours(1);
        if (minimalDateEvent.isAfter(event.getEventDate())) {
            throw new ValidationException("До времени начала события меньше одного часа");
        }
        if (event.getState() != State.PENDING) {
            throw new ValidationException("Событие не в состоянии ожидания публикации");
        }
        event.setPublishedOn(dateNow);
        event.setState(State.PUBLISHED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        if (event.getState() == State.PUBLISHED) {
            throw new ValidationException("Событие уже опубликовано");
        }
        event.setState(State.CANCELED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> findAllEventByPublicApi(HttpServletRequest request, String text, Long[] categories,
                                                       Boolean paid, String rangeStart, String rangeEnd,
                                                       boolean onlyAvailable, String sort, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByState(State.PUBLISHED);
        if (text != null) {
            events = events.stream().filter(event -> event.getAnnotation().toLowerCase().contains(text.toLowerCase())
                    || event.getDescription().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
        }
        if (categories != null) {
            List<Long> categoriesId = Arrays.asList(categories);
            events = events.stream()
                    .filter(event -> categoriesId.contains(event.getCategory().getId())).collect(Collectors.toList());
        }
        if (paid != null) {
            events = events.stream().filter(event -> event.isPaid() == paid).collect(Collectors.toList());
        }
        if (rangeStart == null && rangeEnd == null) {
            events = events.stream().filter(event -> event.getEventDate().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else {
            events = filterEventByDate(rangeStart, rangeEnd, events);
        }
        if (onlyAvailable) {
            events = events.stream().filter(event -> event.getParticipantLimit() == 0
                    || event.getParticipantLimit() > event.getConfirmedRequests()).collect(Collectors.toList());
        }
        if (sort.equals("VIEWS")) {
            events = events.stream().sorted(Comparator.comparingInt(Event::getViews)).collect(Collectors.toList());
        } else {
            events = events.stream().sorted((o1, o2) -> {
                if (o1.getEventDate().isAfter(o2.getEventDate())) {
                    return 1;
                } else if (o1.getEventDate().isBefore(o2.getEventDate())) {
                    return -1;
                } else {
                    return 0;
                }
            }).collect(Collectors.toList());
        }
        events = events.stream().skip(from).limit(size).collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeNow = LocalDateTime.now();
        EndpointHit endpointHit = new EndpointHit(null, "ewm-server", request.getRequestURI(),
                request.getRemoteAddr(), timeNow.format(formatter));
        eventClient.createHit(endpointHit);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto findEvent(Long id, HttpServletRequest request) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", id));
        }
        Event event = optionalEvent.get();
        if(!event.getState().equals(State.PUBLISHED)){
            throw new ValidationException("Событие еще не опубликовано");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.of(2020,5,5,0,0);
        LocalDateTime end = LocalDateTime.of(2035,5,5,0,0);
        LocalDateTime timeNow = LocalDateTime.now();
        String[] uris = new String[1];
        uris[0] = request.getRequestURI();
        ResponseEntity<Object> responseEntity = eventClient.getStats(start.format(formatter),
                end.format(formatter), uris);
        List<LinkedHashMap<String,Object>> list = (List<LinkedHashMap<String, Object>>) responseEntity.getBody();
        LinkedHashMap<String, Object> linkedHashMap = list.get(0);
        String string = linkedHashMap.get("hits").toString();
        event.setViews(Integer.parseInt(string));
        EndpointHit endpointHit = new EndpointHit(null, "ewm-server", request.getRequestURI(),
                request.getRemoteAddr(), timeNow.format(formatter));
        eventClient.createHit(endpointHit);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> findAllEventByUser(Long userId, Integer from, Integer size) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        List<Event> events = eventRepository.findAllByInitiator(user);
        events = events.stream().skip(from).limit(size).collect(Collectors.toList());
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, UpdateEventRequest request) {
        Event event = validateUserAndEvent(userId, request.getEventId());
        if(event.getState() == State.PUBLISHED){
            throw new ValidationException("Событие уже опубликовано");
        }
        if(request.getEventDate() != null){
            event.setEventDate(request.getEventDate());
        }
        LocalDateTime minimalDateEvent = LocalDateTime.now().plusHours(2);
        LocalDateTime eventDate = event.getEventDate();
        if (minimalDateEvent.isAfter(eventDate)) {
            throw new ValidationException("До времени начала события меньше двух часов");
        }
        event.setState(State.PENDING);
        if(request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if(request.getCategory() != null){
            Optional<Category> optionalCategory = categoryRepository.findById(request.getCategory());
            if(optionalCategory.isEmpty()){
                throw new CategoryNotFoundException
                        (String.format("Категории с id %d не найдено", request.getCategory()));
            }
            Category category = optionalCategory.get();
            event.setCategory(category);
        }
        if(request.getDescription() != null){
            event.setDescription(request.getDescription());
        }
        if(request.getPaid() != null){
            event.setPaid(request.getPaid());
        }
        if(request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if(request.getTitle() != null){
            event.setTitle(request.getTitle());
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findEventByUser(Long userId, Long eventId) {
        Event event = validateUserAndEvent(userId, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto canceledEventByUser(Long userId, Long eventId) {
        Event event = validateUserAndEvent(userId, eventId);
        if(event.getState() == State.PUBLISHED){
            throw new ValidationException("Событие уже опубликовано");
        }
        event.setState(State.CANCELED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private Event validateUserAndEvent(Long userId, Long eventId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if(optionalEvent.isEmpty()){
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        if(!event.getInitiator().equals(user)){
            throw new ValidationException("Событие не принадлежит текущему поьзователю");
        }
        return event;
    }
}
