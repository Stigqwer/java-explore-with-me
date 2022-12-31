package ru.practicum.ewm.compilation;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

import javax.validation.Valid;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class CompilationController {
    private CompilationService compilationService;

    @PostMapping("/admin/compilations")
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto compilationDto){
        return compilationService.createCompilation(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId){
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compId,
                                           @PathVariable Long eventId){
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public void putEventFromCompilation(@PathVariable Long compId,
                                        @PathVariable Long eventId){
        compilationService.putEventFromCompilation(compId, eventId);
    }

    @DeleteMapping("/admin/compilations/{compId}/pin")
    public void deletePinCompilation(@PathVariable Long compId){
        compilationService.deletePinCompilation(compId);
    }

    @PatchMapping("/admin/compilations/{compId}/pin")
    public void pinCompilation(@PathVariable Long compId){
        compilationService.pinCompilation(compId);
    }
}
