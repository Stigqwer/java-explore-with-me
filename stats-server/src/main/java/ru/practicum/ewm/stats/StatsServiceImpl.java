package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService{
    private final StatsRepository statsRepository;
    @Override
    public void createHit(EndpointHit endpointHit) {
        statsRepository.save(StatsMapper.toHit(endpointHit));
    }

    @Override
    public List<ViewStats> findAllStatsWithDate(String start, String end, String[] uris, boolean unique) {
        LocalDateTime dateStart =
                LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime dateEnd =
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<ViewStats> viewStatsList = new ArrayList<>();
        for(String uri: uris){
            List<Hit> hits = statsRepository.findAll();
            ViewStats viewStats = new ViewStats();
            viewStats.setApp("ewm-server");
            viewStats.setUri(uri);
            if(unique){
                viewStats.setHits(statsRepository.countDistinctByUriAndTimestampBetween(uri, dateStart, dateEnd) + 1);
            } else {
                viewStats.setHits(statsRepository.countByUriAndTimestampBetween(uri, dateStart, dateEnd) + 1);
            }
            viewStatsList.add(viewStats);
        }
        return viewStatsList;
    }
}
