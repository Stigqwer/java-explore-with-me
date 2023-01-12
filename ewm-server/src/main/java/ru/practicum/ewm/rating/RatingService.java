package ru.practicum.ewm.rating;

public interface RatingService {
    Rating addLike(Long userId, Long eventId);
}
