package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface StatsRepository extends JpaRepository <Hit, Long>{
    @Query("select count(h) from Hit h where h.uri = ?1 and h.timestamp between ?2 and ?3")
    int countByUriAndTimestampBetween(String uri, LocalDateTime timestampStart, LocalDateTime timestampEnd);
    int countDistinctByUriAndTimestampBetween(String uri, LocalDateTime timestampStart, LocalDateTime timestampEnd);
}
