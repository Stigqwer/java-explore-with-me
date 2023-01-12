package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
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
        List<String> uriList = Arrays.asList(uris);
        List<Integer> hits;
        if (unique) {
            hits = statsRepository.countDistinctByUriIsInAndTimestampBetween(uriList, dateStart, dateEnd);
        } else {
            hits = statsRepository.countByUriIsInAndTimestampBetween(uriList, dateStart, dateEnd);
        }
        List<ViewStats> viewStatsList = new ArrayList<>();
        int countList = 0;
        for (String uri : uris) {
            ViewStats viewStats = new ViewStats();
            viewStats.setApp("ewm-server");
            viewStats.setUri(uri);
            viewStats.setHits(hits.get(countList) + 1);
            viewStatsList.add(viewStats);
            countList++;
        }
        return viewStatsList;
    }
}
