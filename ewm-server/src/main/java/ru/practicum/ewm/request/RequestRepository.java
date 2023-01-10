package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByUserAndEvent(User user, Event event);

    List<Request> findAllByUser(User user);

    List<Request> findAllByEvent(Event event);

}
