package co.jtmail.service.impl;

import co.jtmail.dto.request.CreateAttachmentRequest;
import co.jtmail.dto.response.AttachmentResponse;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.mapper.AttachmentMapper;
import co.jtmail.model.Attachment;
import co.jtmail.model.Email;
import co.jtmail.repository.AttachmentRepository;
import co.jtmail.repository.EmailRepository;
import co.jtmail.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final EmailRepository emailRepository;

    @Override
    public List<AttachmentResponse> getAttachmentsByEmail(UUID emailId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email", emailId));

        return attachmentRepository.findByEmail(email)
                .stream()
                .map(AttachmentMapper::toResponse)
                .toList();
    }

    @Override
    public AttachmentResponse getAttachmentById(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", id));
        return AttachmentMapper.toResponse(attachment);
    }

    @Override
    public AttachmentResponse createAttachment(CreateAttachmentRequest request) {
        Email email = emailRepository.findById(request.getEmailId())
                .orElseThrow(() -> new ResourceNotFoundException("Email", request.getEmailId()));

        Attachment attachment = Attachment.builder()
                .email(email)
                .fileName(request.getFileName())
                .fileSize(request.getFileSize())
                .mimeType(request.getMimeType())
                .storageUrl(request.getStorageUrl())
                .build();

        return AttachmentMapper.toResponse(attachmentRepository.save(attachment));
    }

    @Override
    public void deleteAttachment(UUID id) {
        if (!attachmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attachment", id);
        }
        attachmentRepository.deleteById(id);
    }
}