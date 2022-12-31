package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventNotFoundException;
import ru.practicum.ewm.event.EventRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImp implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        List<Long> eventsId = compilationDto.getEvents();
        List<Event> events = eventRepository.findAll().stream()
                .filter(event -> eventsId.contains(event.getId())).collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilationRepository
                .save(CompilationMapper.toCompilation(compilationDto, events)));
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
            Compilation compilation = findCompilationById(compId);
            List<Event> events = compilation.getEvents().stream()
                    .filter(event -> !Objects.equals(event.getId(), eventId)).collect(Collectors.toList());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
    }

    @Override
    public void putEventFromCompilation(Long compId, Long eventId) {
            Compilation compilation = findCompilationById(compId);
            Optional<Event> optionalEvent = eventRepository.findById(eventId);
            if (optionalEvent.isEmpty()) {
                throw new EventNotFoundException(String.format("Событие с id %d найдено", eventId));
            }

            List<Event> events = compilation.getEvents();
            events.add(optionalEvent.get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);

    }

    @Override
    public void deletePinCompilation(Long compId) {
        Compilation compilation = findCompilationById(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public void pinCompilation(Long compId) {
        Compilation compilation = findCompilationById(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    private Compilation findCompilationById(Long compId) {
        Optional<Compilation> optionalCompilation = compilationRepository.findById(compId);
        if (optionalCompilation.isEmpty()) {
            throw new CompilationNotFoundException(String.format("Подборка с id %d найдена", compId));
        } else {
            return optionalCompilation.get();
        }
    }
}
