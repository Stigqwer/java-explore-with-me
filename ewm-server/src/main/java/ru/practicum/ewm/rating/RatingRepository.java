package ru.practicum.ewm.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    @Query("select r from rating r where r.id.eventId = ?1")
    List<Rating> findAllByEvent(Long eventId);
}
