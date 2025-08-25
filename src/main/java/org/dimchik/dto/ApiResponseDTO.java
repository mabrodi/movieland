package org.dimchik.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseDTO<T> {
    private String status;
    private int code;
    private T data;

    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .code(200)
                .status("SUCCESS")
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> response(T data, int code, String status) {
        return ApiResponseDTO.<T>builder()
                .code(code)
                .status(status)
                .data(data)
                .build();
    }
}
