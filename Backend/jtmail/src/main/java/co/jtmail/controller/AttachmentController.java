package co.jtmail.controller;

import co.jtmail.dto.request.CreateAttachmentRequest;
import co.jtmail.dto.response.AttachmentResponse;
import co.jtmail.service.AttachmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/email/{emailId}")
    public ResponseEntity<List<AttachmentResponse>> getByEmail(@PathVariable UUID emailId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByEmail(emailId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttachmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(attachmentService.getAttachmentById(id));
    }

    @PostMapping
    public ResponseEntity<AttachmentResponse> create(
            @Valid @RequestBody CreateAttachmentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.createAttachment(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}