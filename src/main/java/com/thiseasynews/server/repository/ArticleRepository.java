package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository
        extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    /**
     * 기사 단건 상세 조회 (media, category fetch join)
     */
    @Query("SELECT a FROM Article a " +
           "LEFT JOIN FETCH a.media " +
           "LEFT JOIN FETCH a.category " +
           "WHERE a.id = :id AND a.statusCode = 'PUBLISHED'")
    Optional<Article> findPublishedById(@Param("id") Long id);

    /**
     * 키워드 연관 기사 목록 (media, category fetch join)
     * summary_keyword → news_summary → article 경로
     */
    @Query("SELECT a FROM Article a " +
           "LEFT JOIN FETCH a.media " +
           "LEFT JOIN FETCH a.category " +
           "WHERE a.summary IS NOT NULL " +
           "AND a.summary.id IN (" +
           "  SELECT sk.summary.id FROM SummaryKeyword sk WHERE sk.keyword.id = :keywordId" +
           ") AND a.statusCode = 'PUBLISHED' " +
           "ORDER BY a.publishedAt DESC")
    List<Article> findTopByKeywordId(@Param("keywordId") Integer keywordId, Pageable pageable);

    /**
     * 키워드 연관 기사 수
     */
    @Query("SELECT COUNT(a) FROM Article a " +
           "WHERE a.summary IS NOT NULL " +
           "AND a.summary.id IN (" +
           "  SELECT sk.summary.id FROM SummaryKeyword sk WHERE sk.keyword.id = :keywordId" +
           ") AND a.statusCode = 'PUBLISHED'")
    long countByKeywordId(@Param("keywordId") Integer keywordId);
}
