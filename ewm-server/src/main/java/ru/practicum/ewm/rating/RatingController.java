package ru.practicum.ewm.rating;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@AllArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/users/{userId}/events/{eventId}/like")
    public Rating addLike(@PathVariable Long userId, @PathVariable Long eventId){
        return ratingService.addLike(userId, eventId);
    }
}
