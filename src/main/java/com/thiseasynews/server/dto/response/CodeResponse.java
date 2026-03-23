package com.thiseasynews.server.dto.response;

import com.thiseasynews.server.entity.CommonDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "공통 코드 응답 (카테고리 · 언론사)")
public class CodeResponse {

    @Schema(description = "코드 ID (예: MED_CHOSUN, CAT_POLITICS)")
    private String id;

    @Schema(description = "코드 이름 (예: 조선일보, 정치)")
    private String name;

    @Schema(description = "부가 값 (필요 시 사용)")
    private String codeValue;

    public static CodeResponse from(CommonDetail detail) {
        return CodeResponse.builder()
                .id(detail.getId())
                .name(detail.getName())
                .codeValue(detail.getCodeValue())
                .build();
    }
}
