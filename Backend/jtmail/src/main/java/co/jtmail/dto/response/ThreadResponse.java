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
public class ThreadResponse {
    private UUID idThread;
    private Instant createdAt;
    private Instant lastEmailAt;
}