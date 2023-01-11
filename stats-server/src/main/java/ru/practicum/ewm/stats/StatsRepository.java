package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    List<Integer> countByUriIsInAndTimestampBetween(List<String> uris, LocalDateTime timestamp, LocalDateTime timestamp2);

    List<Integer> countDistinctByUriIsInAndTimestampBetween(List<String> uris, LocalDateTime timestampStart, LocalDateTime timestampEnd);

}
