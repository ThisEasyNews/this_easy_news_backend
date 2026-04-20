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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SummaryService {

    private final NewsSummaryRepository newsSummaryRepository;

    // ── 오늘의 브리핑 ─────────────────────────────────
    @Cacheable(value = RedisConfig.CACHE_TODAY_BRIEFING, key = "'today'")
    public List<BriefingResponse> getTodayBriefing() {
        LocalDate today = LocalDate.now();
        List<NewsSummary> briefings = newsSummaryRepository.findBriefingByTargetDateOrderByCreatedAtDesc(today);
        if (briefings.isEmpty()) {
            throw new BusinessException(ErrorCode.BRIEFING_NOT_READY);
        }
        return briefings.stream().limit(10).map(BriefingResponse::from).toList();
    }

    // ── 날짜별 브리핑 ─────────────────────────────────
    public List<BriefingResponse> getBriefingByDate(LocalDate date) {
        List<NewsSummary> briefings = newsSummaryRepository.findBriefingByTargetDateOrderByCreatedAtDesc(date);
        if (briefings.isEmpty()) {
            throw new BusinessException(
                    date.equals(LocalDate.now())
                            ? ErrorCode.BRIEFING_NOT_READY
                            : ErrorCode.BRIEFING_NOT_FOUND);
        }
        return briefings.stream().limit(10).map(BriefingResponse::from).toList();
    }

    // ── 브리핑 상세 ───────────────────────────────────
    @Cacheable(value = RedisConfig.CACHE_BRIEFING_DETAIL, key = "#id")
    @Transactional
    public BriefingResponse getBriefingDetail(Long id) {
        NewsSummary briefing = newsSummaryRepository.findBriefingDetailById(id)
                .stream().findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.BRIEFING_NOT_FOUND));

        newsSummaryRepository.incrementViewCount(id);

        return BriefingResponse.from(briefing);
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
