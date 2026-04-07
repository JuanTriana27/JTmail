package co.jtmail.mapper;

import co.jtmail.dto.response.AttachmentResponse;
import co.jtmail.model.Attachment;

public class AttachmentMapper {

    public static AttachmentResponse toResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .idAttachment(attachment.getIdAttachment())
                .emailId(attachment.getEmail().getIdEmail())
                .fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize())
                .mimeType(attachment.getMimeType())
                .storageUrl(attachment.getStorageUrl())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}