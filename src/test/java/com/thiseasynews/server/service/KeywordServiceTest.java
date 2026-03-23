package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.response.KeywordResponse;
import com.thiseasynews.server.entity.KeywordLog;
import com.thiseasynews.server.entity.NewsKeyword;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.repository.KeywordLogRepository;
import com.thiseasynews.server.repository.NewsKeywordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {

    @InjectMocks
    private KeywordService keywordService;

    @Mock
    private KeywordLogRepository  keywordLogRepository;

    @Mock
    private NewsKeywordRepository newsKeywordRepository;

    @Test
    @DisplayName("핫 키워드 조회 시 1번부터 순위가 부여된다")
    void getHotKeywords_rankAssigned() {
        List<KeywordLog> logs = List.of(
                makeLog("KW_AI",      "AI",   320),
                makeLog("KW_ECONOMY", "경제", 280),
                makeLog("KW_STOCK",   "주식", 200)
        );
        given(keywordLogRepository.findTopByTargetDate(any(LocalDate.class), any()))
                .willReturn(logs);

        List<KeywordResponse> result = keywordService.getHotKeywords();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getRank()).isEqualTo(1);
        assertThat(result.get(0).getKeyword()).isEqualTo("AI");
        assertThat(result.get(0).getMentionCount()).isEqualTo(320);
        assertThat(result.get(1).getRank()).isEqualTo(2);
        assertThat(result.get(2).getRank()).isEqualTo(3);
    }

    @Test
    @DisplayName("핫 키워드 없으면 빈 리스트 반환")
    void getHotKeywords_empty() {
        given(keywordLogRepository.findTopByTargetDate(any(), any()))
                .willReturn(List.of());

        List<KeywordResponse> result = keywordService.getHotKeywords();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하는 키워드 ID 검증 성공")
    void getKeywordOrThrow_success() {
        NewsKeyword kw = makeKeyword("KW_AI", "AI");
        given(newsKeywordRepository.findByIdAndStatusCode("KW_AI", "PUBLISHED"))
                .willReturn(Optional.of(kw));

        NewsKeyword result = keywordService.getKeywordOrThrow("KW_AI");

        assertThat(result.getKeyword()).isEqualTo("AI");
    }

    @Test
    @DisplayName("없는 키워드 ID 검증 시 KEYWORD_NOT_FOUND 예외")
    void getKeywordOrThrow_notFound() {
        given(newsKeywordRepository.findByIdAndStatusCode(anyString(), anyString()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> keywordService.getKeywordOrThrow("INVALID"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.KEYWORD_NOT_FOUND));
    }

    // ── 헬퍼 ──────────────────────────────────────────
    private NewsKeyword makeKeyword(String id, String keyword) {
        NewsKeyword kw = new NewsKeyword();
        ReflectionTestUtils.setField(kw, "id",         id);
        ReflectionTestUtils.setField(kw, "keyword",    keyword);
        ReflectionTestUtils.setField(kw, "statusCode", "PUBLISHED");
        return kw;
    }

    private KeywordLog makeLog(String kwId, String kwName, int count) {
        NewsKeyword kw  = makeKeyword(kwId, kwName);
        KeywordLog  log = new KeywordLog();
        ReflectionTestUtils.setField(log, "keyword",      kw);
        ReflectionTestUtils.setField(log, "targetDate",   LocalDate.now());
        ReflectionTestUtils.setField(log, "mentionCount", count);
        return log;
    }
}
