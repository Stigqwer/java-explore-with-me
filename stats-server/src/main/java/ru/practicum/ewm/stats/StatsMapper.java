package ru.practicum.ewm.stats;

import ru.practicum.ewm.stats.dto.EndpointHit;

public class StatsMapper {
    public static Hit toHit(EndpointHit endpointHit) {
        Hit hit = new Hit();
        hit.setApp(endpointHit.getApp());
        hit.setUri(endpointHit.getUri());
        hit.setIp(endpointHit.getIp());
        hit.setTimestamp(endpointHit.getTimestamp());
        return hit;
    }
}
