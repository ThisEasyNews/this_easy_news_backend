package com.thiseasynews.server.dto.response;

import com.thiseasynews.server.entity.Article;
import com.thiseasynews.server.entity.KeywordLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "핫 키워드 응답")
public class KeywordResponse {

    @Schema(description = "키워드 ID (문자열)")
    private String id;

    @Schema(description = "키워드 텍스트")
    private String keyword;

    @Schema(description = "오늘 언급 횟수")
    private Integer mentionCount;

    @Schema(description = "관련 기사 전체 수")
    private int relatedArticleCount;

    @Schema(description = "관련 기사 목록 (최대 5건)")
    private List<RelatedArticle> relatedArticles;

    public static KeywordResponse of(KeywordLog log, List<Article> articles) {
        return KeywordResponse.builder()
                .id(String.valueOf(log.getKeyword().getId()))
                .keyword(log.getKeyword().getKeyword())
                .mentionCount(log.getMentionCount())
                .relatedArticleCount(articles.size())
                .relatedArticles(articles.stream().map(RelatedArticle::from).toList())
                .build();
    }

    @Getter
    @Builder
    @Schema(description = "관련 기사")
    public static class RelatedArticle {

        @Schema(description = "기사 ID")
        private Long id;

        @Schema(description = "기사 제목")
        private String originalTitle;

        @Schema(description = "언론사명")
        private String mediaName;

        @Schema(description = "카테고리명")
        private String categoryName;

        @Schema(description = "발행 일시")
        private LocalDateTime publishedAt;

        @Schema(description = "원본 URL")
        private String url;

        public static RelatedArticle from(Article article) {
            return RelatedArticle.builder()
                    .id(article.getId())
                    .originalTitle(article.getOriginalTitle())
                    .mediaName(article.getMedia() != null ? article.getMedia().getName() : null)
                    .categoryName(article.getCategory() != null ? article.getCategory().getName() : null)
                    .publishedAt(article.getPublishedAt())
                    .url(article.getUrl())
                    .build();
        }
    }
}
