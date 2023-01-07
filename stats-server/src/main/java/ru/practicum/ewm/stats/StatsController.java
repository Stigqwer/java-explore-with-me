package ru.practicum.ewm.stats;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.stats.dto.EndpointHit;

import javax.validation.Valid;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public void createHit(@RequestBody @Valid EndpointHit endpointHit){
        statsService.createHit(endpointHit);
    }
}
