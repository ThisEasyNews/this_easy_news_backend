package com.thiseasynews.server.controller;

import com.thiseasynews.server.dto.response.BatchLogResponse;
import com.thiseasynews.server.global.common.ApiResponse;
import com.thiseasynews.server.service.BatchLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배치 관리 (Admin)", description = "배치 실행 이력 조회 API - 내부/관리자 전용")
@RestController
@RequestMapping("/api/admin/batch-logs")
@RequiredArgsConstructor
public class BatchLogController {

    private final BatchLogService batchLogService;

    // ── 최근 배치 이력 ────────────────────────────────
    @Operation(
            summary = "최근 배치 실행 이력 조회",
            description = "최근 N건의 배치 실행 로그를 반환합니다. (기본 20건)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<BatchLogResponse>>> getRecentLogs(
            @Parameter(description = "조회 건수 (기본 20)") @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(batchLogService.getRecentLogs(limit)));
    }

    // ── Job명별 이력 ──────────────────────────────────
    @Operation(
            summary = "Job명별 배치 이력 조회",
            description = "특정 Job의 전체 실행 이력을 반환합니다. "
                        + "(예: RSS_CRAWLING, GPT_SUMMARY, DAILY_BRIEFING)"
    )
    @GetMapping("/jobs/{jobName}")
    public ResponseEntity<ApiResponse<List<BatchLogResponse>>> getLogsByJobName(
            @Parameter(description = "배치 Job 이름") @PathVariable String jobName) {
        return ResponseEntity.ok(ApiResponse.ok(batchLogService.getLogsByJobName(jobName)));
    }
}
