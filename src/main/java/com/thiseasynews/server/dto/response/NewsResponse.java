package com.thiseasynews.server.dto.response;

import com.thiseasynews.server.entity.Article;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "뉴스 기사 응답")
public class NewsResponse {

    @Schema(description = "기사 ID")
    private Long id;

    @Schema(description = "기사 제목")
    private String originalTitle;

    @Schema(description = "원본 URL")
    private String url;

    @Schema(description = "기사 본문 (상세 조회 시에만 포함)")
    private String content;

    @Schema(description = "발행 일시")
    private LocalDateTime publishedAt;

    @Schema(description = "언론사 ID")
    private String mediaId;

    @Schema(description = "언론사명")
    private String mediaName;

    @Schema(description = "카테고리 ID")
    private String categoryId;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "연관 요약 ID")
    private Long summaryId;

    // ── 목록용 (content 제외) ────────────────────────
    public static NewsResponse ofList(Article article) {
        return NewsResponse.builder()
                .id(article.getId())
                .originalTitle(article.getOriginalTitle())
                .url(article.getUrl())
                .publishedAt(article.getPublishedAt())
                .mediaId(article.getMedia()    != null ? article.getMedia().getId()       : null)
                .mediaName(article.getMedia()  != null ? article.getMedia().getName()     : null)
                .categoryId(article.getCategory() != null ? article.getCategory().getId() : null)
                .categoryName(article.getCategory() != null ? article.getCategory().getName() : null)
                .summaryId(article.getSummary() != null ? article.getSummary().getId()    : null)
                .build();
    }

    // ── 상세용 (content 포함) ────────────────────────
    public static NewsResponse ofDetail(Article article) {
        return NewsResponse.builder()
                .id(article.getId())
                .originalTitle(article.getOriginalTitle())
                .url(article.getUrl())
                .content(article.getContent())
                .publishedAt(article.getPublishedAt())
                .mediaId(article.getMedia()    != null ? article.getMedia().getId()       : null)
                .mediaName(article.getMedia()  != null ? article.getMedia().getName()     : null)
                .categoryId(article.getCategory() != null ? article.getCategory().getId() : null)
                .categoryName(article.getCategory() != null ? article.getCategory().getName() : null)
                .summaryId(article.getSummary() != null ? article.getSummary().getId()    : null)
                .build();
    }
}
