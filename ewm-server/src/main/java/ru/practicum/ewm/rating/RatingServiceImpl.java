package ru.practicum.ewm.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService{

    private final RatingRepository ratingRepository;
    @Override
    public Rating addLike(Long userId, Long eventId) {
        return ratingRepository.save(new Rating(new RatingId(eventId, userId), true));
    }
}
