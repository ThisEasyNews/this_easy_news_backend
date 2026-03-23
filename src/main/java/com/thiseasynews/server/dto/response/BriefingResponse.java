package com.thiseasynews.server.dto.response;

import com.thiseasynews.server.entity.NewsSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@Schema(description = "3분 브리핑 응답")
public class BriefingResponse {

    @Schema(description = "브리핑 ID")
    private Long id;

    @Schema(description = "브리핑 제목")
    private String title;

    @Schema(description = "브리핑 요약 내용")
    private String summaryContent;

    @Schema(description = "AI 인사이트")
    private String insight;

    @Schema(description = "사용된 AI 모델")
    private String aiModel;

    @Schema(description = "대표 이미지 URL")
    private String topImageUrl;

    @Schema(description = "조회수")
    private Integer viewCount;

    @Schema(description = "대상 날짜")
    private LocalDate targetDate;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "연관 뉴스 요약 목록 (상세 조회 시에만 포함)")
    private List<SummaryItem> includedSummaries;

    // ── 목록/오늘 브리핑용 (연관 뉴스 제외) ────────
    public static BriefingResponse ofSimple(NewsSummary ns) {
        return BriefingResponse.builder()
                .id(ns.getId())
                .title(ns.getTitle())
                .summaryContent(ns.getSummaryContent())
                .insight(ns.getInsight())
                .topImageUrl(ns.getTopImageUrl())
                .viewCount(ns.getViewCount())
                .targetDate(ns.getTargetDate())
                .createdAt(ns.getCreatedAt())
                .build();
    }

    // ── 상세 조회용 (연관 뉴스 포함) ────────────────
    public static BriefingResponse ofDetail(NewsSummary ns) {
        List<SummaryItem> items = ns.getIncludedSummaries().stream()
                .map(bs -> SummaryItem.from(bs.getSummary()))
                .collect(Collectors.toList());

        return BriefingResponse.builder()
                .id(ns.getId())
                .title(ns.getTitle())
                .summaryContent(ns.getSummaryContent())
                .insight(ns.getInsight())
                .aiModel(ns.getAiModel())
                .topImageUrl(ns.getTopImageUrl())
                .viewCount(ns.getViewCount())
                .targetDate(ns.getTargetDate())
                .createdAt(ns.getCreatedAt())
                .includedSummaries(items)
                .build();
    }

    // ── 내부 중첩 DTO ────────────────────────────────
    @Getter
    @Builder
    @Schema(description = "브리핑에 포함된 뉴스 요약 항목")
    public static class SummaryItem {

        @Schema(description = "요약 ID")
        private Long id;

        @Schema(description = "요약 제목")
        private String title;

        @Schema(description = "요약 내용")
        private String summaryContent;

        @Schema(description = "대표 이미지 URL")
        private String topImageUrl;

        public static SummaryItem from(NewsSummary ns) {
            return SummaryItem.builder()
                    .id(ns.getId())
                    .title(ns.getTitle())
                    .summaryContent(ns.getSummaryContent())
                    .topImageUrl(ns.getTopImageUrl())
                    .build();
        }
    }
}
