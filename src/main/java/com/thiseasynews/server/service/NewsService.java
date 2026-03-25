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
    public PageResponse<NewsResponse> getArticlesByKeyword(Integer keywordId, int page, int size) {
        Specification<Article> spec = ArticleSpecification.published()
                .and(ArticleSpecification.byKeyword(keywordId));
        return fetchPage(spec, page, size);
    }

    // ── 복합 검색 (Specification 동적 조합) ──────────
    public PageResponse<NewsResponse> searchArticles(ArticleSearchRequest req) {
        Specification<Article> spec = ArticleSpecification.published();

        if (req.getMediaId() != null && !req.getMediaId().isBlank()) {
            spec = spec.and(ArticleSpecification.byMedia(req.getMediaId()));
        }
        if (req.getCategoryId() != null && !req.getCategoryId().isBlank()) {
            spec = spec.and(ArticleSpecification.byCategory(req.getCategoryId()));
        }
        if (req.getKeywordId() != null) {
            spec = spec.and(ArticleSpecification.byKeyword(req.getKeywordId()));
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
