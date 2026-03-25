package com.thiseasynews.server.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thiseasynews.server.entity.CommonDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "언론사 응답")
public class PublisherResponse {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Schema(description = "언론사 ID")
    private String id;

    @Schema(description = "언론사명")
    private String name;

    @Schema(description = "아이콘 텍스트 (약어)")
    private String iconText;

    @Schema(description = "브랜드 컬러 (hex)")
    private String color;

    public static PublisherResponse from(CommonDetail detail) {
        String iconText = null;
        String color = null;
        try {
            if (detail.getCodeValue() != null) {
                JsonNode node = mapper.readTree(detail.getCodeValue());
                iconText = node.path("iconText").asText(null);
                color    = node.path("color").asText(null);
            }
        } catch (Exception ignored) {}

        return PublisherResponse.builder()
                .id(detail.getId())
                .name(detail.getName())
                .iconText(iconText)
                .color(color)
                .build();
    }
}
