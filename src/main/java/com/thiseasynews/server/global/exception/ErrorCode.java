package com.thiseasynews.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT("C001", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("C002", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("C003", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Article
    ARTICLE_NOT_FOUND("A001", "기사를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Summary / Briefing
    SUMMARY_NOT_FOUND("S001", "뉴스 요약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BRIEFING_NOT_FOUND("S002", "브리핑을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BRIEFING_NOT_READY("S003", "오늘의 브리핑이 아직 준비되지 않았습니다.", HttpStatus.NOT_FOUND),

    // Keyword
    KEYWORD_NOT_FOUND("K001", "키워드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Common Code
    COMMON_GROUP_NOT_FOUND("CC001", "공통 그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMON_DETAIL_NOT_FOUND("CC002", "공통 항목을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String     code;
    private final String     message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code       = code;
        this.message    = message;
        this.httpStatus = httpStatus;
    }
}
