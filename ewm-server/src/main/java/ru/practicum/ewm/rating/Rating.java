package ru.practicum.ewm.rating;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "rating")
public class Rating {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "eventId", column = @Column(name = "event_id")),
            @AttributeOverride(name = "userId", column = @Column(name = "user_id"))
    })
    private RatingId id;
    private boolean reaction;
}
