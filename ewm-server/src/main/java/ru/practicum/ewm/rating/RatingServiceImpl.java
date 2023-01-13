package ru.practicum.ewm.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.ValidationException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventNotFoundException;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.State;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserNotFoundException;
import ru.practicum.ewm.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public Rating addLike(Long userId, Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        if (event.getInitiator().equals(user)) {
            throw new ValidationException("Нельзя ставить реакцию на свое событие");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Событие не опубликовано");
        }
        Rating rating = ratingRepository.save(new Rating(new RatingId(eventId, userId), true));
        saveRating(event);
        return rating;
    }

    @Override
    public Rating addDislike(Long userId, Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        if (event.getInitiator().equals(user)) {
            throw new ValidationException("нельзя ставить реакцию на свое событие");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Событие не опубликовано");
        }
        Rating rating = ratingRepository.save(new Rating(new RatingId(eventId, userId), false));
        saveRating(event);
        return rating;
    }

    private void saveRating(Event event) {
        int oldRatingEvent = event.getRating();
        int ratingEvent = ratingRepository.countReactionByEvent(event.getId(), true)
                - ratingRepository.countReactionByEvent(event.getId(), false);
        event.setRating(ratingEvent);
        eventRepository.save(event);
        User user = event.getInitiator();
        user.setRating(user.getRating() + ratingEvent - oldRatingEvent);
        userRepository.save(user);
    }

    @Override
    public void deleteReaction(Long userId, Long eventId) {
        RatingId ratingId = new RatingId(eventId, userId);
        Optional<Rating> optionalRating = ratingRepository.findById(ratingId);
        if (optionalRating.isEmpty()) {
            throw new ValidationException(String.format(
                    "Реакции от пользователя с id %d к событию с id %d не найдена", userId, eventId));
        }
        ratingRepository.deleteById(ratingId);
        Event event = eventRepository.findById(ratingId.getEventId()).get();
        saveRating(event);
    }
}