package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "article",
       indexes = {
           @Index(name = "idx_article_published_at", columnList = "published_at DESC"),
           @Index(name = "idx_article_media_id",     columnList = "media_id"),
           @Index(name = "idx_article_category_id",  columnList = "category_id")
       })
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_id")
    private NewsSummary summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id")
    private CommonDetail media;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CommonDetail category;

    @Column(name = "original_title", nullable = false, length = 1000)
    private String originalTitle;

    @Column(name = "url", nullable = false, unique = true, length = 5000)
    private String url;

    @Column(name = "feedparser_content", columnDefinition = "TEXT")
    private String feedparserContent;

    @Column(name = "crawler_content", columnDefinition = "TEXT")
    private String crawlerContent;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 5000)
    private String imageUrl;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "scraped_at")
    private LocalDateTime scrapedAt;

    @Column(name = "is_summarized")
    private Boolean isSummarized = false;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;
}
