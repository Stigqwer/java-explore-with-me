package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService{
    private final StatsRepository statsRepository;
    @Override
    public void createHit(EndpointHit endpointHit) {
        statsRepository.save(StatsMapper.toHit(endpointHit));
    }
}
