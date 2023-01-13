package ru.practicum.ewm.rating;


public interface RatingService {
    Rating addLike(Long userId, Long eventId);

    Rating addDislike(Long userId, Long eventId);

    void deleteReaction(Long userId, Long eventId);
}
