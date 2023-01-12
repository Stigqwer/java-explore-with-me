package ru.practicum.ewm.stats;

import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.util.List;

public interface StatsService {
    void createHit(EndpointHit endpointHit);

    List<ViewStats> findAllStatsWithDate(String start, String end, String[] uris, boolean unique);
}
