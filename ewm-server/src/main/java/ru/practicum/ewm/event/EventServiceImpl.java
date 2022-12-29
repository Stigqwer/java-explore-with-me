package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryNotFoundException;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.error.ValidationException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserNotFoundException;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

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
        if(minimalDateEvent.isAfter(eventDate)){
            throw new ValidationException("До времени начала события меньше двух часов");
        }
        Category category = null;
        if(eventDto.getCategoryId() != null) {
            Optional<Category> optionalCategory = categoryRepository.findById(eventDto.getCategoryId());
            if (optionalCategory.isEmpty()) {
                throw new CategoryNotFoundException(String.format("Категория с id %d не найдена",
                        eventDto.getCategoryId()));
            }
            category = optionalCategory.get();
        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        Location location = eventDto.getLocation();
        Optional<Location> optionalLocation = locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        if(optionalLocation.isEmpty()){
            locationRepository.save(location);
        }
        Event event = EventMapper.toEvent(eventDto, category, user, location);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        if(category != null){
            eventFullDto.setCategory(CategoryMapper.toCategoryDto(category));
        }
        return eventFullDto;
    }
}
