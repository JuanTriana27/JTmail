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
public class AttachmentResponse {
    private UUID idAttachment;
    private UUID emailId;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String storageUrl;
    private Instant createdAt;
}