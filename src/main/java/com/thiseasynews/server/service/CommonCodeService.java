package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.response.CategoryResponse;
import com.thiseasynews.server.dto.response.CodeResponse;
import com.thiseasynews.server.dto.response.PublisherResponse;
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
    @Cacheable(value = RedisConfig.CACHE_CODE_LIST, key = "'media'")
    public List<PublisherResponse> getMediaList() {
        validateGroup(GROUP_MEDIA);
        return commonDetailRepository.findPublishedByGroupId(GROUP_MEDIA).stream()
                .map(PublisherResponse::from)
                .toList();
    }

    // ── 카테고리 목록 ─────────────────────────────────
    @Cacheable(value = RedisConfig.CACHE_CODE_LIST, key = "'category'")
    public List<CategoryResponse> getCategoryList() {
        validateGroup(GROUP_CATEGORY);
        return commonDetailRepository.findPublishedByGroupId(GROUP_CATEGORY).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    // ── 그룹 ID 기반 범용 조회 ────────────────────────
    public List<CodeResponse> getDetailsByGroup(String groupId) {
        validateGroup(groupId);
        return commonDetailRepository.findPublishedByGroupId(groupId).stream()
                .map(CodeResponse::from)
                .toList();
    }

    private void validateGroup(String groupId) {
        commonGroupRepository.findByIdAndStatusCode(groupId, "PUBLISHED")
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_GROUP_NOT_FOUND));
    }
}
