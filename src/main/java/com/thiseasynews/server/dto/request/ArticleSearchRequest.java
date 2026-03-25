package com.thiseasynews.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 기사 목록 조회 공통 요청 파라미터
 * - mediaId, categoryId, keywordId 중 하나를 받아 Specification 조합에 사용
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "기사 목록 검색 조건")
public class ArticleSearchRequest {

    @Schema(description = "언론사 ID (예: MED_CHOSUN)", example = "MED_CHOSUN")
    private String mediaId;

    @Schema(description = "카테고리 ID (예: CAT_POLITICS)", example = "CAT_POLITICS")
    private String categoryId;

    @Schema(description = "키워드 ID")
    private Integer keywordId;

    @Min(0)
    @Schema(description = "페이지 번호 (0부터)", defaultValue = "0")
    private int page = 0;

    @Min(1) @Max(20)
    @Schema(description = "페이지 크기 (최대 20)", defaultValue = "20")
    private int size = 20;

}
