package co.jtmail.service;

import co.jtmail.dto.request.CreateAttachmentRequest;
import co.jtmail.dto.response.AttachmentResponse;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    List<AttachmentResponse> getAttachmentsByEmail(UUID emailId);
    AttachmentResponse getAttachmentById(UUID id);
    AttachmentResponse createAttachment(CreateAttachmentRequest request);
    void deleteAttachment(UUID id);
}