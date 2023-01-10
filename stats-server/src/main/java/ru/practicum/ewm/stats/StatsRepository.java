package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    int countByUriAndTimestampBetween(String uri, LocalDateTime timestampStart, LocalDateTime timestampEnd);

    int countDistinctByUriAndTimestampBetween(String uri, LocalDateTime timestampStart, LocalDateTime timestampEnd);
}
