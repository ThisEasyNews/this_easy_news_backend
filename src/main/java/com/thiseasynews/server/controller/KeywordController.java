package com.thiseasynews.server.controller;

import com.thiseasynews.server.dto.response.KeywordResponse;
import com.thiseasynews.server.dto.response.NewsResponse;
import com.thiseasynews.server.global.common.ApiResponse;
import com.thiseasynews.server.global.common.PageResponse;
import com.thiseasynews.server.service.KeywordService;
import com.thiseasynews.server.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "핫 키워드", description = "핫 키워드 Top10 및 키워드 통계 조회 API")
@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;
    private final NewsService    newsService;

    // ── 오늘의 핫 키워드 Top10 ────────────────────────
    @Operation(
            summary = "오늘의 핫 키워드 Top10",
            description = "오늘 날짜 기준 언급 빈도 상위 10개 키워드를 반환합니다. Redis 캐시(10분) 적용."
    )
    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getHotKeywords() {
        return ResponseEntity.ok(ApiResponse.ok(keywordService.getHotKeywords()));
    }

    // ── 날짜별 핫 키워드 ──────────────────────────────
    @Operation(
            summary = "날짜별 핫 키워드 Top10",
            description = "특정 날짜의 핫 키워드를 반환합니다."
    )
    @GetMapping("/hot/date/{date}")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getHotKeywordsByDate(
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", example = "2024-05-20")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok(keywordService.getHotKeywordsByDate(date)));
    }

    // ── 키워드 관련 기사 목록 ─────────────────────────
    @Operation(
            summary = "키워드 관련 기사 목록",
            description = "해당 키워드와 연관된 기사를 최신순으로 페이징 반환합니다."
    )
    @GetMapping("/{keywordId}/articles")
    public ResponseEntity<ApiResponse<PageResponse<NewsResponse>>> getArticlesByKeyword(
            @Parameter(description = "키워드 ID") @PathVariable Integer keywordId,
            @Parameter(description = "페이지 번호 (0부터)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (최대 20)")  @RequestParam(defaultValue = "20") int size) {
        keywordService.getKeywordOrThrow(keywordId);
        return ResponseEntity.ok(ApiResponse.ok(newsService.getArticlesByKeyword(keywordId, page, size)));
    }
}
