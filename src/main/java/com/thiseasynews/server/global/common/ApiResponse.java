package com.thiseasynews.server.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final ErrorInfo error;

    private ApiResponse(boolean success, String message, T data, ErrorInfo error) {
        this.success = success;
        this.message = message;
        this.data    = data;
        this.error   = error;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "success", data, null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, null, null, new ErrorInfo(code, message));
    }

    @Getter
    public static class ErrorInfo {
        private final String code;
        private final String message;

        public ErrorInfo(String code, String message) {
            this.code    = code;
            this.message = message;
        }
    }
}
