package com.thiseasynews.server.repository;

import com.thiseasynews.server.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
