package com.thiseasynews.server.dto.response;

import com.thiseasynews.server.entity.BatchLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "배치 실행 이력 응답")
public class BatchLogResponse {

    private Long          id;
    private String        jobName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String        statusCode;
    private Integer       totalCount;
    private Integer       successCount;
    private Integer       failCount;
    private String        errorMessage;
    private Integer       executionTimeSec;
    private LocalDateTime createdAt;

    public static BatchLogResponse from(BatchLog log) {
        return BatchLogResponse.builder()
                .id(log.getId())
                .jobName(log.getJobName())
                .startTime(log.getStartTime())
                .endTime(log.getEndTime())
                .statusCode(log.getStatusCode())
                .totalCount(log.getTotalCount())
                .successCount(log.getSuccessCount())
                .failCount(log.getFailCount())
                .errorMessage(log.getErrorMessage())
                .executionTimeSec(log.getExecutionTimeSec())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
