package co.jtmail.dto.response;

import co.jtmail.model.enums.EmailStatus;
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
public class EmailResponse {
    private UUID idEmail;
    private UUID threadId;
    private UUID senderId;
    private String senderName;
    private String subject;
    private String body;
    private EmailStatus status;
    private Instant sentAt;
    private Instant createdAt;
}