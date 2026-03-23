package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.response.CodeResponse;
import com.thiseasynews.server.entity.CommonDetail;
import com.thiseasynews.server.global.config.RedisConfig;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.repository.CommonDetailRepository;
import com.thiseasynews.server.repository.CommonGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonCodeService {

    private static final String GROUP_MEDIA    = "MEDIA";
    private static final String GROUP_CATEGORY = "CATEGORY";

    private final CommonGroupRepository  commonGroupRepository;
    private final CommonDetailRepository commonDetailRepository;

    // ── 언론사 목록 ───────────────────────────────────
    /**
     * Redis 캐시(1시간) 적용
     */
    @Cacheable(value = RedisConfig.CACHE_CODE_LIST, key = "'media'")
    public List<CodeResponse> getMediaList() {
        return getDetailsByGroup(GROUP_MEDIA);
    }

    // ── 카테고리 목록 ─────────────────────────────────
    /**
     * Redis 캐시(1시간) 적용
     */
    @Cacheable(value = RedisConfig.CACHE_CODE_LIST, key = "'category'")
    public List<CodeResponse> getCategoryList() {
        return getDetailsByGroup(GROUP_CATEGORY);
    }

    // ── 그룹 ID 기반 범용 조회 ────────────────────────
    public List<CodeResponse> getDetailsByGroup(String groupId) {
        // 그룹 존재 여부 먼저 검증
        commonGroupRepository.findByIdAndStatusCode(groupId, "PUBLISHED")
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_GROUP_NOT_FOUND));

        List<CommonDetail> details = commonDetailRepository.findPublishedByGroupId(groupId);
        return details.stream()
                .map(CodeResponse::from)
                .collect(Collectors.toList());
    }
}
