package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.BatchLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BatchLogRepository extends JpaRepository<BatchLog, Long> {

    /** 최근 N건 조회 */
    Page<BatchLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** Job 이름별 이력 조회 */
    List<BatchLog> findByJobNameOrderByCreatedAtDesc(String jobName);

    /** 기간 내 실패/부분성공 이력 조회 (모니터링용) */
    @Query("SELECT b FROM BatchLog b " +
           "WHERE b.statusCode IN ('FAIL', 'PARTIAL_SUCCESS') " +
           "AND b.startTime BETWEEN :from AND :to " +
           "ORDER BY b.startTime DESC")
    List<BatchLog> findFailedJobsBetween(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);
}
