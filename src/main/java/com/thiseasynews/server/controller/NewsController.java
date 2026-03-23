package com.thiseasynews.server.controller;

import com.thiseasynews.server.dto.request.ArticleSearchRequest;
import com.thiseasynews.server.dto.response.NewsResponse;
import com.thiseasynews.server.global.common.ApiResponse;
import com.thiseasynews.server.global.common.PageResponse;
import com.thiseasynews.server.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "뉴스 기사", description = "기사 조회 · 언론사/카테고리별 필터링 API")
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // ── 기사 단건 상세 ────────────────────────────────
    @Operation(summary = "기사 상세 조회", description = "기사 ID로 본문 포함 상세 내용을 반환합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> getArticle(
            @Parameter(description = "기사 ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.getArticle(id)));
    }

    // ── 언론사별 기사 목록 ────────────────────────────
    @Operation(
            summary = "언론사별 기사 목록",
            description = "특정 언론사의 기사를 최신순으로 페이징 반환합니다."
    )
    @GetMapping("/media/{mediaId}")
    public ResponseEntity<ApiResponse<PageResponse<NewsResponse>>> getArticlesByMedia(
            @Parameter(description = "언론사 ID (예: MED_CHOSUN)") @PathVariable String mediaId,
            @Parameter(description = "페이지 번호 (0부터)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (최대 20)")  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.getArticlesByMedia(mediaId, page, size)));
    }

    // ── 카테고리별 기사 목록 ──────────────────────────
    @Operation(
            summary = "카테고리별 기사 목록",
            description = "선택한 카테고리의 기사를 최신순으로 페이징 반환합니다."
    )
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<NewsResponse>>> getArticlesByCategory(
            @Parameter(description = "카테고리 ID (예: CAT_POLITICS)") @PathVariable String categoryId,
            @Parameter(description = "페이지 번호 (0부터)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (최대 20)")  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.getArticlesByCategory(categoryId, page, size)));
    }

    // ── 복합 검색 (Specification 동적 조합) ──────────
    @Operation(
            summary = "기사 복합 검색",
            description = "mediaId · categoryId · keywordId 조건을 AND로 조합해 동적 검색합니다. "
                        + "조건을 지정하지 않으면 전체 게시 기사를 반환합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<NewsResponse>>> searchArticles(
            @Valid ArticleSearchRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.searchArticles(request)));
    }
}
