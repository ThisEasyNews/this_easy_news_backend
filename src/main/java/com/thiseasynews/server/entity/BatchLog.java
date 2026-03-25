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
@Table(name = "batch_log")
public class BatchLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 예: 'RSS_CRAWLING', 'GPT_SUMMARY', 'DAILY_BRIEFING' */
    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** 'RUNNING' | 'SUCCESS' | 'PARTIAL_SUCCESS' | 'FAIL' */
    @Column(name = "status_code", nullable = false, length = 20)
    private String statusCode;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "fail_count")
    private Integer failCount;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_time_sec")
    private Integer executionTimeSec;

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
