package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto compilationDto);
    void deleteCompilation(Long compId);
    void deleteEventFromCompilation(Long compId, Long eventId);
    void putEventFromCompilation(Long compId, Long eventId);
    void deletePinCompilation(Long compId);
    void pinCompilation(Long compId);
}
