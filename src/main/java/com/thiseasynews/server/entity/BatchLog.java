package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BATCH_LOG")
public class BatchLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 예: 'RSS_CRAWLING', 'GPT_SUMMARY', 'DAILY_BRIEFING' */
    @Column(name = "JOB_NAME", nullable = false, length = 100)
    private String jobName;

    @Column(name = "START_TIME", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    /** 'RUNNING' | 'SUCCESS' | 'PARTIAL_SUCCESS' | 'FAIL' */
    @Column(name = "STATUS_CODE", nullable = false, length = 20)
    private String statusCode;

    @Column(name = "TOTAL_COUNT")
    private Integer totalCount;

    @Column(name = "SUCCESS_COUNT")
    private Integer successCount;

    @Column(name = "FAIL_COUNT")
    private Integer failCount;

    @Column(name = "ERROR_MESSAGE", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "EXECUTION_TIME_SEC")
    private Integer executionTimeSec;

    // ── 팩토리 메서드 ────────────────────────────────
    public static BatchLog startJob(String jobName) {
        return BatchLog.builder()
                .jobName(jobName)
                .startTime(LocalDateTime.now())
                .statusCode("RUNNING")
                .totalCount(0)
                .successCount(0)
                .failCount(0)
                .build();
    }

    // ── 상태 변경 (변경 감지) ────────────────────────
    public void complete(int total, int success, int fail) {
        this.endTime          = LocalDateTime.now();
        this.totalCount       = total;
        this.successCount     = success;
        this.failCount        = fail;
        this.statusCode       = (fail == 0) ? "SUCCESS"
                              : (success > 0) ? "PARTIAL_SUCCESS"
                              : "FAIL";
        this.executionTimeSec = (int) java.time.Duration
                .between(this.startTime, this.endTime).getSeconds();
    }

    public void fail(String errorMessage) {
        this.endTime          = LocalDateTime.now();
        this.statusCode       = "FAIL";
        this.errorMessage     = errorMessage;
        this.executionTimeSec = (int) java.time.Duration
                .between(this.startTime, this.endTime).getSeconds();
    }
}
