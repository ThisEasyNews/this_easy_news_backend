package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.NewsSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface NewsSummaryRepository extends JpaRepository<NewsSummary, Long> {

    /**
     * 오늘의 브리핑 조회 (BRIEFING 타입 + targetDate 기준)
     */
    @Query("SELECT ns FROM NewsSummary ns " +
           "WHERE ns.summaryType = 'BRIEFING' " +
           "AND ns.targetDate = :targetDate " +
           "AND ns.statusCode = 'PUBLISHED'")
    Optional<NewsSummary> findBriefingByTargetDate(@Param("targetDate") LocalDate targetDate);

    /**
     * 브리핑 상세 조회 - 연관 GENERAL 요약 목록 함께 fetch
     */
    @Query("SELECT DISTINCT ns FROM NewsSummary ns " +
           "LEFT JOIN FETCH ns.includedSummaries bs " +
           "LEFT JOIN FETCH bs.summary s " +
           "WHERE ns.id = :id " +
           "AND ns.summaryType = 'BRIEFING' " +
           "AND ns.statusCode = 'PUBLISHED'")
    Optional<NewsSummary> findBriefingDetailById(@Param("id") Long id);

    /**
     * 조회수 증가 (벌크 update, 변경 감지 대신 단일 쿼리)
     */
    @Modifying
    @Query("UPDATE NewsSummary ns SET ns.viewCount = ns.viewCount + 1 WHERE ns.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
