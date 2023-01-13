package ru.practicum.ewm.rating;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@AllArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/users/{userId}/events/{eventId}/like")
    public Rating addLike(@PathVariable Long userId, @PathVariable Long eventId) {
        return ratingService.addLike(userId, eventId);
    }

    @PostMapping("/users/{userId}/events/{eventId}/dislike")
    public Rating addDisLike(@PathVariable Long userId, @PathVariable Long eventId) {
        return ratingService.addDislike(userId, eventId);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/delReaction")
    public void deleteReaction(@PathVariable Long userId, @PathVariable Long eventId) {
        ratingService.deleteReaction(userId, eventId);
    }
}
