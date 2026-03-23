package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.response.BriefingResponse;
import com.thiseasynews.server.entity.NewsSummary;
import com.thiseasynews.server.global.config.RedisConfig;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.repository.NewsSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SummaryService {

    private final NewsSummaryRepository newsSummaryRepository;

    // ── 오늘의 브리핑 ─────────────────────────────────
    /**
     * 오늘 날짜 기준 브리핑 조회
     * Redis 캐시(30분) 적용 - 배치 완료 후 evictTodayBriefing() 호출로 갱신
     */
    @Cacheable(value = RedisConfig.CACHE_TODAY_BRIEFING, key = "'today'")
    public BriefingResponse getTodayBriefing() {
        return getBriefingByDate(LocalDate.now());
    }

    // ── 날짜별 브리핑 ─────────────────────────────────
    public BriefingResponse getBriefingByDate(LocalDate date) {
        NewsSummary briefing = newsSummaryRepository.findBriefingByTargetDate(date)
                .orElseThrow(() -> new BusinessException(
                        date.equals(LocalDate.now())
                                ? ErrorCode.BRIEFING_NOT_READY
                                : ErrorCode.BRIEFING_NOT_FOUND));
        return BriefingResponse.ofSimple(briefing);
    }

    // ── 브리핑 상세 ───────────────────────────────────
    /**
     * 브리핑 상세 + 연관 GENERAL 요약 목록 반환
     * Redis 캐시(30분) 적용, 조회수 증가 처리
     */
    @Cacheable(value = RedisConfig.CACHE_BRIEFING_DETAIL, key = "#id")
    @Transactional
    public BriefingResponse getBriefingDetail(Long id) {
        NewsSummary briefing = newsSummaryRepository.findBriefingDetailById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRIEFING_NOT_FOUND));

        // 캐시 미스(최초 조회) 시에만 실행 → 정밀한 카운트가 필요하면 별도 Redis counter 사용 고려
        newsSummaryRepository.incrementViewCount(id);

        return BriefingResponse.ofDetail(briefing);
    }

    // ── 캐시 무효화 (배치 완료 후 호출) ─────────────────
    @CacheEvict(value = RedisConfig.CACHE_TODAY_BRIEFING, key = "'today'")
    public void evictTodayBriefing() {
        log.info("[SummaryService] 오늘의 브리핑 캐시 무효화");
    }

    @CacheEvict(value = RedisConfig.CACHE_BRIEFING_DETAIL, key = "#id")
    public void evictBriefingDetail(Long id) {
        log.info("[SummaryService] 브리핑 상세 캐시 무효화 id={}", id);
    }
}
