package co.jtmail.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabelResponse {
    private UUID idLabel;
    private String name;
    private String color;
    private Boolean isSystem;
    private Instant createdAt;

    // No podemos exponer el user completo — solo su ID para referencia
    private UUID userId;
}