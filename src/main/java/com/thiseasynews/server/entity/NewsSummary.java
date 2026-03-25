package com.thiseasynews.server.entity;

import com.thiseasynews.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "news_summary",
       indexes = @Index(name = "idx_summary_type_date", columnList = "summary_type, target_date"))
public class NewsSummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 'GENERAL' | 'BRIEFING' */
    @Column(name = "summary_type", nullable = false, length = 50)
    private String summaryType;

    @Column(name = "title", nullable = false, length = 1000)
    private String title;

    @Column(name = "summary_content", nullable = false, columnDefinition = "TEXT")
    private String summaryContent;

    @Column(name = "insight", columnDefinition = "TEXT")
    private String insight;

    @Column(name = "ai_model", length = 50)
    private String aiModel;

    @Column(name = "top_image_url", length = 5000)
    private String topImageUrl;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    /** BRIEFING 타입일 때 연관된 GENERAL 요약들 */
    @OneToMany(mappedBy = "briefing", fetch = FetchType.LAZY)
    private List<BriefingSummary> includedSummaries = new ArrayList<>();

    /** 이 요약을 참조하는 기사들 (article.summary_id → news_summary.id) */
    @OneToMany(mappedBy = "summary", fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    /** 이 요약에 매핑된 키워드들 */
    @OneToMany(mappedBy = "summary", fetch = FetchType.LAZY)
    private List<SummaryKeyword> summaryKeywords = new ArrayList<>();

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
}
