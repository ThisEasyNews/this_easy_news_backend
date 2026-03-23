package com.thiseasynews.server.dto.response;

import com.thiseasynews.server.entity.KeywordLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "핫 키워드 순위 응답")
public class KeywordResponse {

    @Schema(description = "순위 (1부터 시작)")
    private int rank;

    @Schema(description = "키워드 ID")
    private String keywordId;

    @Schema(description = "키워드 텍스트")
    private String keyword;

    @Schema(description = "오늘 언급 횟수")
    private Integer mentionCount;

    public static KeywordResponse of(int rank, KeywordLog log) {
        return KeywordResponse.builder()
                .rank(rank)
                .keywordId(log.getKeyword().getId())
                .keyword(log.getKeyword().getKeyword())
                .mentionCount(log.getMentionCount())
                .build();
    }
}
