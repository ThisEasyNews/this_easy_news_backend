package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.response.KeywordResponse;
import com.thiseasynews.server.entity.KeywordLog;
import com.thiseasynews.server.entity.NewsKeyword;
import com.thiseasynews.server.global.config.RedisConfig;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.repository.KeywordLogRepository;
import com.thiseasynews.server.repository.NewsKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

    private static final int HOT_KEYWORD_LIMIT = 10;

    private final KeywordLogRepository  keywordLogRepository;
    private final NewsKeywordRepository newsKeywordRepository;

    // ── 오늘의 핫 키워드 Top10 ────────────────────────
    /**
     * Redis 캐시(10분) 적용
     */
    @Cacheable(value = RedisConfig.CACHE_HOT_KEYWORDS, key = "'today'")
    public List<KeywordResponse> getHotKeywords() {
        return getHotKeywordsByDate(LocalDate.now());
    }

    // ── 날짜별 핫 키워드 Top10 ────────────────────────
    public List<KeywordResponse> getHotKeywordsByDate(LocalDate date) {
        List<KeywordLog> logs = keywordLogRepository.findTopByTargetDate(
                date, PageRequest.of(0, HOT_KEYWORD_LIMIT));

        List<KeywordResponse> result = new ArrayList<>();
        for (int i = 0; i < logs.size(); i++) {
            result.add(KeywordResponse.of(i + 1, logs.get(i)));
        }
        return result;
    }

    // ── 키워드 단건 검증 (기사 목록 조회 전 사전 확인) ──
    public NewsKeyword getKeywordOrThrow(String keywordId) {
        return newsKeywordRepository.findByIdAndStatusCode(keywordId, "PUBLISHED")
                .orElseThrow(() -> new BusinessException(ErrorCode.KEYWORD_NOT_FOUND));
    }
}
