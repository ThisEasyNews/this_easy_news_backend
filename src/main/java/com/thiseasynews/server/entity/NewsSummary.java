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
@Table(name = "NEWS_SUMMARY",
       indexes = @Index(name = "idx_summary_type_date", columnList = "SUMMARY_TYPE, TARGET_DATE"))
public class NewsSummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 'GENERAL' | 'BRIEFING' */
    @Column(name = "SUMMARY_TYPE", nullable = false, length = 50)
    private String summaryType;

    @Column(name = "TITLE", nullable = false, length = 1000)
    private String title;

    @Column(name = "SUMMARY_CONTENT", nullable = false, columnDefinition = "TEXT")
    private String summaryContent;

    @Column(name = "INSIGHT", columnDefinition = "TEXT")
    private String insight;

    @Column(name = "AI_MODEL", length = 50)
    private String aiModel;

    @Column(name = "TOP_IMAGE_URL", length = 5000)
    private String topImageUrl;

    @Column(name = "VIEW_COUNT")
    private Integer viewCount = 0;

    /** 브리핑 날짜 식별용 (BRIEFING 타입에서 사용) */
    @Column(name = "TARGET_DATE")
    private LocalDate targetDate;

    @Column(name = "STATUS_CODE", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    /** BRIEFING 타입일 때 연관된 GENERAL 요약들 */
    @OneToMany(mappedBy = "briefing", fetch = FetchType.LAZY)
    private List<BriefingSummary> includedSummaries = new ArrayList<>();

    /** 이 요약에 매핑된 키워드들 */
    @OneToMany(mappedBy = "summary", fetch = FetchType.LAZY)
    private List<SummaryKeyword> summaryKeywords = new ArrayList<>();

    // ── 도메인 메서드 ───────────────────────────────
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
}
