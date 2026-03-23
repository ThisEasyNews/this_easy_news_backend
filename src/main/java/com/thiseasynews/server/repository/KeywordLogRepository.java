package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.KeywordLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface KeywordLogRepository extends JpaRepository<KeywordLog, Long> {

    /**
     * 특정 날짜 핫 키워드 Top N
     * mentionCount 내림차순, keyword fetch join
     */
    @Query("SELECT kl FROM KeywordLog kl " +
           "JOIN FETCH kl.keyword k " +
           "WHERE kl.targetDate = :targetDate " +
           "AND k.statusCode = 'PUBLISHED' " +
           "ORDER BY kl.mentionCount DESC")
    List<KeywordLog> findTopByTargetDate(
            @Param("targetDate") LocalDate targetDate,
            Pageable pageable);
}
