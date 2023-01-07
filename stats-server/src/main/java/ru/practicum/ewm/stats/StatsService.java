package ru.practicum.ewm.stats;

import ru.practicum.ewm.stats.dto.EndpointHit;

public interface StatsService {
    void createHit(EndpointHit endpointHit);
}
