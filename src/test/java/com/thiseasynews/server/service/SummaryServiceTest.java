package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.response.BriefingResponse;
import com.thiseasynews.server.entity.BriefingSummary;
import com.thiseasynews.server.entity.NewsSummary;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.repository.NewsSummaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @InjectMocks
    private SummaryService summaryService;

    @Mock
    private NewsSummaryRepository newsSummaryRepository;

    // ── getTodayBriefing ────────────────────────────
    @Test
    @DisplayName("오늘 브리핑 존재 시 BriefingResponse 반환")
    void getTodayBriefing_success() {
        NewsSummary briefing = makeBriefing(1L, LocalDate.now(), List.of());
        given(newsSummaryRepository.findBriefingByTargetDate(LocalDate.now()))
                .willReturn(List.of(briefing));

        BriefingResponse result = summaryService.getTodayBriefing();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("오늘의 브리핑");
    }

    @Test
    @DisplayName("오늘 브리핑 미준비 시 BRIEFING_NOT_READY 예외")
    void getTodayBriefing_notReady() {
        given(newsSummaryRepository.findBriefingByTargetDate(any()))
                .willReturn(List.of());

        assertThatThrownBy(() -> summaryService.getTodayBriefing())
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.BRIEFING_NOT_READY));
    }

    // ── getBriefingByDate ───────────────────────────
    @Test
    @DisplayName("과거 날짜 브리핑 없을 시 BRIEFING_NOT_FOUND 예외")
    void getBriefingByDate_pastNotFound() {
        LocalDate past = LocalDate.now().minusDays(3);
        given(newsSummaryRepository.findBriefingByTargetDate(past))
                .willReturn(List.of());

        assertThatThrownBy(() -> summaryService.getBriefingByDate(past))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.BRIEFING_NOT_FOUND));
    }

    // ── getBriefingDetail ───────────────────────────
    @Test
    @DisplayName("브리핑 상세 조회 - 연관 요약 포함 반환")
    void getBriefingDetail_success() {
        NewsSummary child    = makeSummary(2L, "연관 뉴스 요약");
        NewsSummary briefing = makeBriefing(1L, LocalDate.now(), List.of(child));

        given(newsSummaryRepository.findBriefingDetailById(1L))
                .willReturn(List.of(briefing));
        willDoNothing().given(newsSummaryRepository).incrementViewCount(1L);

        BriefingResponse result = summaryService.getBriefingDetail(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSummaries()).hasSize(1);
        assertThat(result.getSummaries().get(0).getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("존재하지 않는 브리핑 상세 조회 시 BRIEFING_NOT_FOUND 예외")
    void getBriefingDetail_notFound() {
        given(newsSummaryRepository.findBriefingDetailById(anyLong()))
                .willReturn(List.of());

        assertThatThrownBy(() -> summaryService.getBriefingDetail(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.BRIEFING_NOT_FOUND));
    }

    // ── 헬퍼 ──────────────────────────────────────────
    private NewsSummary makeBriefing(Long id, LocalDate date, List<NewsSummary> children) {
        NewsSummary ns = new NewsSummary();
        ReflectionTestUtils.setField(ns, "id",             id);
        ReflectionTestUtils.setField(ns, "summaryType",    "BRIEFING");
        ReflectionTestUtils.setField(ns, "title",          "오늘의 브리핑");
        ReflectionTestUtils.setField(ns, "summaryContent", "브리핑 내용");
        ReflectionTestUtils.setField(ns, "statusCode",     "PUBLISHED");
        ReflectionTestUtils.setField(ns, "targetDate",     date);
        ReflectionTestUtils.setField(ns, "viewCount",      0);

        List<BriefingSummary> mappings = children.stream().map(child -> {
            BriefingSummary bs = new BriefingSummary();
            ReflectionTestUtils.setField(bs, "briefing", ns);
            ReflectionTestUtils.setField(bs, "summary",  child);
            return bs;
        }).toList();
        ReflectionTestUtils.setField(ns, "includedSummaries", mappings);
        return ns;
    }

    private NewsSummary makeSummary(Long id, String title) {
        NewsSummary ns = new NewsSummary();
        ReflectionTestUtils.setField(ns, "id",             id);
        ReflectionTestUtils.setField(ns, "summaryType",    "GENERAL");
        ReflectionTestUtils.setField(ns, "title",          title);
        ReflectionTestUtils.setField(ns, "summaryContent", "요약 내용");
        ReflectionTestUtils.setField(ns, "statusCode",     "PUBLISHED");
        ReflectionTestUtils.setField(ns, "viewCount",      0);
        ReflectionTestUtils.setField(ns, "includedSummaries", List.of());
        return ns;
    }
}
