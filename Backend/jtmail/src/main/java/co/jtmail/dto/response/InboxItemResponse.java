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
public class InboxItemResponse {
    private UUID idRecipient;
    private UUID emailId;
    private String subject;
    private String senderName;
    private String senderEmail;
    private Boolean isRead;
    private Boolean isStarred;
    private Boolean isArchived;
    private Boolean isTrashed;
    private Instant sentAt;
}