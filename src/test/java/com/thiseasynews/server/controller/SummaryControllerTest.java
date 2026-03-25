package com.thiseasynews.server.controller;

import com.thiseasynews.server.dto.response.BriefingResponse;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.service.SummaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SummaryController.class)
class SummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SummaryService summaryService;

    @Test
    @DisplayName("GET /api/summaries/briefings/today - 오늘 브리핑 200")
    void getTodayBriefing_200() throws Exception {
        BriefingResponse response = BriefingResponse.builder()
                .id(1L)
                .title("오늘의 3분 브리핑")
                .keywords(List.of())
                .imageUrl(null)
                .summaries(List.of())
                .build();

        given(summaryService.getTodayBriefing()).willReturn(response);

        mockMvc.perform(get("/api/summaries/briefings/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("오늘의 3분 브리핑"));
    }

    @Test
    @DisplayName("GET /api/summaries/briefings/today - 브리핑 미준비 404")
    void getTodayBriefing_notReady_404() throws Exception {
        given(summaryService.getTodayBriefing())
                .willThrow(new BusinessException(ErrorCode.BRIEFING_NOT_READY));

        mockMvc.perform(get("/api/summaries/briefings/today"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("S003"));
    }

    @Test
    @DisplayName("GET /api/summaries/briefings/{id} - 브리핑 상세 (연관 뉴스 포함) 200")
    void getBriefingDetail_200() throws Exception {
        BriefingResponse response = BriefingResponse.builder()
                .id(1L)
                .title("오늘의 3분 브리핑")
                .keywords(List.of())
                .summaries(List.of(
                        BriefingResponse.BriefingSummaryItem.builder()
                                .id(2L).title("AI 관련 뉴스").summaryContent("AI 요약").build()
                ))
                .build();

        given(summaryService.getBriefingDetail(1L)).willReturn(response);

        mockMvc.perform(get("/api/summaries/briefings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.summaries").isArray())
                .andExpect(jsonPath("$.data.summaries[0].id").value(2));
    }

    @Test
    @DisplayName("GET /api/summaries/briefings/{id} - 없는 브리핑 404")
    void getBriefingDetail_404() throws Exception {
        given(summaryService.getBriefingDetail(anyLong()))
                .willThrow(new BusinessException(ErrorCode.BRIEFING_NOT_FOUND));

        mockMvc.perform(get("/api/summaries/briefings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("S002"));
    }

    @Test
    @DisplayName("GET /api/summaries/briefings/date/{date} - 날짜별 브리핑 200")
    void getBriefingByDate_200() throws Exception {
        BriefingResponse response = BriefingResponse.builder()
                .id(2L)
                .title("2024-05-20 브리핑")
                .keywords(List.of())
                .summaries(List.of())
                .build();

        given(summaryService.getBriefingByDate(LocalDate.of(2024, 5, 20))).willReturn(response);

        mockMvc.perform(get("/api/summaries/briefings/date/2024-05-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2));
    }
}
