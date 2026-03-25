package com.thiseasynews.server.controller;

import com.thiseasynews.server.dto.response.BriefingResponse;
import com.thiseasynews.server.global.common.ApiResponse;
import com.thiseasynews.server.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "3분 브리핑", description = "뉴스 요약 및 3분 브리핑 조회 API")
@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    // ── 오늘의 브리핑 ─────────────────────────────────
    @Operation(
            summary = "오늘의 3분 브리핑",
            description = "오늘 날짜 기준 브리핑을 반환합니다. Redis 캐시(30분) 적용."
    )
    @GetMapping("/briefings/today")
    public ResponseEntity<ApiResponse<List<BriefingResponse>>> getTodayBriefing() {
        return ResponseEntity.ok(ApiResponse.ok(summaryService.getTodayBriefing()));
    }

    // ── 날짜별 브리핑 ─────────────────────────────────
    @Operation(
            summary = "날짜별 브리핑 조회",
            description = "특정 날짜의 브리핑을 조회합니다. (예: 2024-05-20)"
    )
    @GetMapping("/briefings/date/{date}")
    public ResponseEntity<ApiResponse<List<BriefingResponse>>> getBriefingByDate(
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", example = "2024-05-20")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok(summaryService.getBriefingByDate(date)));
    }

    // ── 브리핑 상세 ───────────────────────────────────
    @Operation(
            summary = "브리핑 상세 조회",
            description = "브리핑 ID로 상세 내용 및 연관 뉴스 요약 목록을 반환합니다. "
                        + "Redis 캐시(30분) 적용, 조회수 자동 증가."
    )
    @GetMapping("/briefings/{id}")
    public ResponseEntity<ApiResponse<BriefingResponse>> getBriefingDetail(
            @Parameter(description = "브리핑 ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(summaryService.getBriefingDetail(id)));
    }
}
