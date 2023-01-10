package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.ValidationException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventNotFoundException;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.State;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserNotFoundException;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        Optional<Request> requestRepeat = requestRepository.findByUserAndEvent(user, event);
        if (requestRepeat.isPresent()) {
            throw new ValidationException("Такой запрос уже существует");
        }
        if (user.equals(event.getInitiator())) {
            throw new ValidationException("Нельзя подать заявку на участие в своем событии");
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ValidationException("Событие еще не опубликовано");
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ValidationException("Достигнут лимит участников в событии");
        }
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
            Request request = RequestMapper.toRequest(event, user);
            request.setStatus(Status.CONFIRMED);
            return RequestMapper.toRequestDto(requestRepository.save(request));
        }
        return RequestMapper.toRequestDto(requestRepository.save(RequestMapper.toRequest(event, user)));
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        List<Request> requests = requestRepository.findAllByUser(user);
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new RequestNotFoundException(String.format("Заявка с id %d не найдена", requestId));
        }
        Request request = optionalRequest.get();
        if (!user.equals(request.getUser())) {
            throw new ValidationException("Заявка не принадлежит данному пользователю");
        }
        if (request.getStatus() == Status.REJECTED) {
            throw new ValidationException("Заявка уже отменена");
        }
        if (request.getStatus() == Status.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByEvent(Long userId, Long eventId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        if(!event.getInitiator().equals(user)){
            throw new ValidationException("Событие не принадлежит текущему пользователю");
        }
        return requestRepository.findAllByEvent(event).stream()
                .map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        if(!event.getInitiator().equals(user)){
            throw new ValidationException("Событие не принадлежит текущему пользователю");
        }
        Optional<Request> optionalRequest = requestRepository.findById(reqId);
        if (optionalRequest.isEmpty()) {
            throw new RequestNotFoundException(String.format("Заявка с id %d не найдена", reqId));
        }
        Request request = optionalRequest.get();
        if (!user.equals(event.getInitiator())) {
            throw new ValidationException("Пользователь не организатор данного мероприятия");
        }
        request.setStatus(Status.CONFIRMED);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        User user = optionalUser.get();
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException(String.format("Событие с id %d не найдено", eventId));
        }
        Event event = optionalEvent.get();
        if(!event.getInitiator().equals(user)){
            throw new ValidationException("Событие не принадлежит текущему пользователю");
        }
        Optional<Request> optionalRequest = requestRepository.findById(reqId);
        if (optionalRequest.isEmpty()) {
            throw new RequestNotFoundException(String.format("Заявка с id %d не найдена", reqId));
        }
        Request request = optionalRequest.get();
        if (!user.equals(event.getInitiator())) {
            throw new ValidationException("Пользователь не организатор данного мероприятия");
        }
        request.setStatus(Status.REJECTED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }
}
