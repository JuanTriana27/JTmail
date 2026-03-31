package co.jtmail.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        String message,
        List<String> details,
        LocalDateTime timestamp
) {
    // Factory method para no repetir el timestamp en cada llamada
    public static ErrorResponse of(String message, List<String> details) {
        return new ErrorResponse(message, details, LocalDateTime.now());
    }
}