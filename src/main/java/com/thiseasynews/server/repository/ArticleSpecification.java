package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.Article;
import com.thiseasynews.server.entity.SummaryKeyword;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

/**
 * Article 조회용 JPA Specification 모음
 *
 * <pre>
 * 사용 예시:
 * Specification<Article> spec = ArticleSpecification.published()
 *         .and(ArticleSpecification.byMedia("MED_CHOSUN"));
 * Page<Article> result = articleRepository.findAll(spec, pageable);
 * </pre>
 */
public final class ArticleSpecification {

    private ArticleSpecification() {}

    /** 게시 상태인 기사만 */
    public static Specification<Article> published() {
        return (root, query, cb) ->
                cb.equal(root.get("statusCode"), "PUBLISHED");
    }

    /** 특정 언론사 필터 */
    public static Specification<Article> byMedia(String mediaId) {
        return (root, query, cb) ->
                cb.equal(root.get("media").get("id"), mediaId);
    }

    /** 특정 카테고리 필터 */
    public static Specification<Article> byCategory(String categoryId) {
        return (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
    }

    /**
     * 특정 키워드와 연관된 기사 필터
     * ARTICLE → NEWS_SUMMARY → SUMMARY_KEYWORD → NEWS_KEYWORD 경로로 서브쿼리
     */
    public static Specification<Article> byKeyword(Integer keywordId) {
        return (root, query, cb) -> {
            // 중복 제거 (SUMMARY_KEYWORD가 여러 개일 때 기사 중복 방지)
            query.distinct(true);

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<SummaryKeyword> skRoot = subquery.from(SummaryKeyword.class);
            subquery.select(skRoot.get("summary").get("id"))
                    .where(cb.equal(skRoot.get("keyword").get("id"), keywordId));

            // article.summary.id IN (subquery)
            return root.get("summary").get("id").in(subquery);
        };
    }

    /** 최신순 정렬 (Specification + Sort 대신 직접 조건에 추가할 때) */
    public static Specification<Article> orderByPublishedAtDesc() {
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("publishedAt")));
            return cb.conjunction(); // 항상 참 (정렬만 추가)
        };
    }
}
