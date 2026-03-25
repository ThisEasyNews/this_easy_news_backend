package com.thiseasynews.server.dto.response;

import com.thiseasynews.server.entity.NewsSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "브리핑 응답")
public class BriefingResponse {

    @Schema(description = "브리핑 ID")
    private Long id;

    @Schema(description = "브리핑 제목")
    private String title;

    @Schema(description = "연관 키워드 목록")
    private List<String> keywords;

    @Schema(description = "대표 이미지 URL")
    private String imageUrl;

    @Schema(description = "포함된 뉴스 요약 목록 (최대 10건)")
    private List<BriefingSummaryItem> summaries;

    public static BriefingResponse from(NewsSummary ns) {
        List<String> keywords = ns.getSummaryKeywords().stream()
                .map(sk -> sk.getKeyword().getKeyword())
                .toList();

        List<BriefingSummaryItem> summaries = ns.getIncludedSummaries().stream()
                .filter(bs -> bs.getSummary() != null)
                .map(bs -> BriefingSummaryItem.from(bs.getSummary()))
                .toList();

        return BriefingResponse.builder()
                .id(ns.getId())
                .title(ns.getTitle())
                .keywords(keywords)
                .imageUrl(ns.getTopImageUrl())
                .summaries(summaries)
                .build();
    }

    @Getter
    @Builder
    @Schema(description = "브리핑에 포함된 뉴스 요약 항목")
    public static class BriefingSummaryItem {

        @Schema(description = "요약 ID")
        private Long id;

        @Schema(description = "뉴스 제목")
        private String title;

        @Schema(description = "AI 요약 내용")
        private String summaryContent;

        @Schema(description = "대표 이미지 URL")
        private String topImageUrl;

        public static BriefingSummaryItem from(NewsSummary ns) {
            return BriefingSummaryItem.builder()
                    .id(ns.getId())
                    .title(ns.getTitle())
                    .summaryContent(ns.getSummaryContent())
                    .topImageUrl(ns.getTopImageUrl())
                    .build();
        }
    }
}
