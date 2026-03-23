package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.request.ArticleSearchRequest;
import com.thiseasynews.server.dto.response.NewsResponse;
import com.thiseasynews.server.entity.Article;
import com.thiseasynews.server.global.common.PageResponse;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.repository.ArticleRepository;
import com.thiseasynews.server.repository.ArticleSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final ArticleRepository articleRepository;

    // ── 기사 단건 상세 ────────────────────────────────
    public NewsResponse getArticle(Long id) {
        Article article = articleRepository.findPublishedById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
        return NewsResponse.ofDetail(article);
    }

    // ── 언론사별 기사 목록 ────────────────────────────
    public PageResponse<NewsResponse> getArticlesByMedia(String mediaId, int page, int size) {
        Specification<Article> spec = ArticleSpecification.published()
                .and(ArticleSpecification.byMedia(mediaId));
        return fetchPage(spec, page, size);
    }

    // ── 카테고리별 기사 목록 ──────────────────────────
    public PageResponse<NewsResponse> getArticlesByCategory(String categoryId, int page, int size) {
        Specification<Article> spec = ArticleSpecification.published()
                .and(ArticleSpecification.byCategory(categoryId));
        return fetchPage(spec, page, size);
    }

    // ── 키워드별 기사 목록 ────────────────────────────
    public PageResponse<NewsResponse> getArticlesByKeyword(String keywordId, int page, int size) {
        Specification<Article> spec = ArticleSpecification.published()
                .and(ArticleSpecification.byKeyword(keywordId));
        return fetchPage(spec, page, size);
    }

    /**
     * 복합 조건 기사 목록 (ArticleSearchRequest 기반)
     * mediaId / categoryId / keywordId 중 입력된 조건을 동적으로 AND 조합
     */
    public PageResponse<NewsResponse> searchArticles(ArticleSearchRequest req) {
        Specification<Article> spec = ArticleSpecification.published();

        // 1. MediaId 체크 (Object -> String 변환)
        String mediaId = (req.getMediaId() != null) ? String.valueOf(req.getMediaId()) : null;
        if (mediaId != null && !mediaId.isBlank()) {
            spec = spec.and(ArticleSpecification.byMedia(mediaId));
        }

        // 2. CategoryId 체크 (Object -> String 변환)
        String categoryId = (req.getCategoryId() != null) ? String.valueOf(req.getCategoryId()) : null;
        if (categoryId != null && !categoryId.isBlank()) {
            spec = spec.and(ArticleSpecification.byCategory(categoryId));
        }

        // 3. KeywordId 체크 (Object -> String 변환)
        String keywordId = (req.getKeywordId() != null) ? String.valueOf(req.getKeywordId()) : null;
        if (keywordId != null && !keywordId.isBlank()) {
            spec = spec.and(ArticleSpecification.byKeyword(keywordId));
        }

        return fetchPage(spec, req.getPage(), req.getSize());
    }

    // ── 공통 페이징 처리 ──────────────────────────────
    private PageResponse<NewsResponse> fetchPage(Specification<Article> spec, int page, int size) {
        PageRequest pageable = PageRequest.of(
                page,
                Math.min(size, 20),
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );
        Page<Article> result = articleRepository.findAll(spec, pageable);
        return PageResponse.of(result.map(NewsResponse::ofList));
    }
}
