package ru.practicum.ewm.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    @Query("select count(r) from rating r where r.id.eventId = ?1 and r.reaction = ?2")
    int countReactionByEvent(Long eventId, boolean reaction);
}
