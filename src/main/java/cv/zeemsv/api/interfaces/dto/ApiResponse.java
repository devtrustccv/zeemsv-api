package cv.zeemsv.api.interfaces.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(String message, T data) { return ApiResponse.<T>builder().success(true).message(message).data(data).build(); }
    public static <T> ApiResponse<T> fail(String message, T data) { return ApiResponse.<T>builder().success(false).message(message).data(data).build(); }
}
