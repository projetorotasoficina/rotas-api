package utfpr.edu.br.coleta.generics.error;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class ApiErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    private Integer status;

    private String error;

    private String message;

    private String path;

    // Campo opcional para erros de validação (BAD_REQUEST)
    private List<String> details;

    public static ApiErrorResponse build(Integer status, String error, String message, String path) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
}