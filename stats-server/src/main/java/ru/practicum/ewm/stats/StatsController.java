package ru.practicum.ewm.stats;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public void createHit(@RequestBody @Valid EndpointHit endpointHit) {
        statsService.createHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> findAllStatsWithDate(@RequestParam String start,
                                                @RequestParam String end,
                                                @RequestParam String[] uris,
                                                @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.findAllStatsWithDate(start, end, uris, unique);
    }
}
