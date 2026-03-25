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
    private String title;

    @Schema(description = "기사 본문 (crawlerContent)")
    private String content;

    @Schema(description = "피드파서 본문")
    private String feedparserContent;

    @Schema(description = "크롤러 본문")
    private String crawlerContent;

    @Schema(description = "기사 설명")
    private String description;

    @Schema(description = "AI 요약 내용")
    private String summary;

    @Schema(description = "언론사 ID")
    private String mediaId;

    @Schema(description = "언론사명")
    private String mediaName;

    @Schema(description = "카테고리 ID")
    private String categoryId;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "발행 일시")
    private LocalDateTime publishedAt;

    @Schema(description = "원본 URL")
    private String url;

    @Schema(description = "대표 이미지 URL")
    private String imageUrl;

    // ── 목록용 (본문 제외) ────────────────────────────
    public static NewsResponse ofList(Article article) {
        return NewsResponse.builder()
                .id(article.getId())
                .title(article.getOriginalTitle())
                .description(article.getDescription())
                .imageUrl(article.getImageUrl())
                .publishedAt(article.getPublishedAt())
                .url(article.getUrl())
                .mediaId(article.getMedia()       != null ? article.getMedia().getId()           : null)
                .mediaName(article.getMedia()     != null ? article.getMedia().getName()         : null)
                .categoryId(article.getCategory() != null ? article.getCategory().getId()       : null)
                .categoryName(article.getCategory() != null ? article.getCategory().getName()   : null)
                .summary(article.getSummary()     != null ? article.getSummary().getSummaryContent() : null)
                .build();
    }

    // ── 상세용 (본문 포함) ────────────────────────────
    public static NewsResponse ofDetail(Article article) {
        return NewsResponse.builder()
                .id(article.getId())
                .title(article.getOriginalTitle())
                .content(article.getCrawlerContent())
                .feedparserContent(article.getFeedparserContent())
                .crawlerContent(article.getCrawlerContent())
                .description(article.getDescription())
                .imageUrl(article.getImageUrl())
                .publishedAt(article.getPublishedAt())
                .url(article.getUrl())
                .mediaId(article.getMedia()       != null ? article.getMedia().getId()           : null)
                .mediaName(article.getMedia()     != null ? article.getMedia().getName()         : null)
                .categoryId(article.getCategory() != null ? article.getCategory().getId()       : null)
                .categoryName(article.getCategory() != null ? article.getCategory().getName()   : null)
                .summary(article.getSummary()     != null ? article.getSummary().getSummaryContent() : null)
                .build();
    }
}
