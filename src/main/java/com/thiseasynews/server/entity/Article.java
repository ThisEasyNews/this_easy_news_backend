package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ARTICLE",
       indexes = {
           @Index(name = "idx_article_published_at", columnList = "PUBLISHED_AT DESC"),
           @Index(name = "idx_article_media_id",     columnList = "MEDIA_ID"),
           @Index(name = "idx_article_category_id",  columnList = "CATEGORY_ID")
       })
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUMMARY_ID")
    private NewsSummary summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEDIA_ID")
    private CommonDetail media;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private CommonDetail category;

    @Column(name = "ORIGINAL_TITLE", nullable = false, length = 1000)
    private String originalTitle;

    @Column(name = "URL", nullable = false, unique = true, length = 5000)
    private String url;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "PUBLISHED_AT")
    private LocalDateTime publishedAt;

    @Column(name = "SCRAPED_AT")
    private LocalDateTime scrapedAt;

    @Column(name = "IS_SUMMARIZED")
    private Boolean isSummarized = false;

    @Column(name = "STATUS_CODE", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;
}
