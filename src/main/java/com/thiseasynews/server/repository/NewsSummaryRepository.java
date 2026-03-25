package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.NewsSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface NewsSummaryRepository extends JpaRepository<NewsSummary, Long> {

    /**
     * 브리핑 조회 (targetDate 기준)
     * - includedSummaries 와 summaryKeywords 를 한 번에 fetch 하면
     *   Hibernate 6 에서 MultipleBagFetchException 이 발생하므로
     *   includedSummaries 만 fetch 하고, 나머지는 트랜잭션 내 lazy loading 으로 처리
     */
    @Query("SELECT DISTINCT ns FROM NewsSummary ns " +
           "LEFT JOIN FETCH ns.includedSummaries bs " +
           "LEFT JOIN FETCH bs.summary " +
           "WHERE ns.summaryType = 'BRIEFING' " +
           "AND ns.targetDate = :targetDate " +
           "AND ns.statusCode = 'PUBLISHED'")
    List<NewsSummary> findBriefingByTargetDate(@Param("targetDate") LocalDate targetDate);

    /**
     * 브리핑 상세 조회 (id 기준) - 동일 이유로 includedSummaries + summary 만 fetch
     */
    @Query("SELECT DISTINCT ns FROM NewsSummary ns " +
           "LEFT JOIN FETCH ns.includedSummaries bs " +
           "LEFT JOIN FETCH bs.summary " +
           "WHERE ns.id = :id " +
           "AND ns.summaryType = 'BRIEFING' " +
           "AND ns.statusCode = 'PUBLISHED'")
    List<NewsSummary> findBriefingDetailById(@Param("id") Long id);

    /**
     * 조회수 증가 (벌크 update, 변경 감지 대신 단일 쿼리)
     */
    @Modifying
    @Query("UPDATE NewsSummary ns SET ns.viewCount = ns.viewCount + 1 WHERE ns.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
