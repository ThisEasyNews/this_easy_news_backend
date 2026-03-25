package com.thiseasynews.server.dto.response;

import com.thiseasynews.server.entity.CommonDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카테고리 응답")
public class CategoryResponse {

    @Schema(description = "카테고리 ID")
    private String id;

    @Schema(description = "카테고리명")
    private String name;

    @Schema(description = "카테고리 표시 텍스트 (선택)")
    private String countText;

    public static CategoryResponse from(CommonDetail detail) {
        return CategoryResponse.builder()
                .id(detail.getId())
                .name(detail.getName())
                .build();
    }
}
