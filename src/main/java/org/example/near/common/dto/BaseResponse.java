package org.example.near.common.dto;

import org.example.near.exception.ErrorCode;
import org.example.near.exception.SuccessCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class BaseResponse<T> {
    private final int code;
    private final String message;
    private T data;

    public static BaseResponse success(SuccessCode success) {
        return new BaseResponse<>(success.getHttpStatusCode(), success.getMessage());
    }

    public static <T> BaseResponse<T> success(SuccessCode success, T data) {
        return new BaseResponse<T>(success.getHttpStatusCode(), success.getMessage(), data);
    }

    public static BaseResponse error(ErrorCode error) {
        return new BaseResponse<>(error.getHttpStatusCode(), error.getMessage());
    }

    public static BaseResponse error(ErrorCode error, String message) {
        return new BaseResponse<>(error.getHttpStatusCode(), message);
    }

}
