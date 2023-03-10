package ru.practicum.ewm.event;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.User;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByState(State state);

    List<Event> findAllByInitiator(User user);

    List<Event> getEventByIdIsIn(List<Long> ids);
}
