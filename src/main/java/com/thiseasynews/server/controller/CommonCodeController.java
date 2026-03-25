package com.thiseasynews.server.controller;

import com.thiseasynews.server.dto.response.CategoryResponse;
import com.thiseasynews.server.dto.response.CodeResponse;
import com.thiseasynews.server.dto.response.PublisherResponse;
import com.thiseasynews.server.global.common.ApiResponse;
import com.thiseasynews.server.service.CommonCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "공통 코드", description = "언론사 · 카테고리 목록 등 공통 코드 조회 API")
@RestController
@RequestMapping("/api/codes")
@RequiredArgsConstructor
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    // ── 언론사 목록 ───────────────────────────────────
    @Operation(
            summary = "언론사 목록 조회",
            description = "등록된 언론사 전체 목록을 반환합니다. Redis 캐시(1시간) 적용."
    )
    @GetMapping("/media")
    public ResponseEntity<ApiResponse<List<PublisherResponse>>> getMediaList() {
        return ResponseEntity.ok(ApiResponse.ok(commonCodeService.getMediaList()));
    }

    // ── 카테고리 목록 ─────────────────────────────────
    @Operation(
            summary = "카테고리 목록 조회",
            description = "등록된 카테고리 전체 목록을 반환합니다. Redis 캐시(1시간) 적용."
    )
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryList() {
        return ResponseEntity.ok(ApiResponse.ok(commonCodeService.getCategoryList()));
    }

    // ── 그룹 ID 기반 범용 코드 조회 ───────────────────
    @Operation(
            summary = "그룹별 공통 코드 조회",
            description = "그룹 ID를 지정해 해당 그룹의 공통 코드 목록을 조회합니다. "
                    + "(예: MEDIA, CATEGORY, STATUS)"
    )
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<ApiResponse<List<CodeResponse>>> getCodesByGroup(
            @Parameter(description = "그룹 ID (예: MEDIA, CATEGORY, STATUS)")
            @PathVariable(name = "groupId") String groupId) { // ("groupId") 이름을 명시적으로 추가
        return ResponseEntity.ok(ApiResponse.ok(commonCodeService.getDetailsByGroup(groupId)));
    }
}