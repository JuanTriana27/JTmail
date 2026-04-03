package co.jtmail.controller;

import co.jtmail.dto.request.SendEmailRequest;
import co.jtmail.dto.response.EmailResponse;
import co.jtmail.dto.response.InboxItemResponse;
import co.jtmail.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // Enviar Correo Nuevo desde un id a otro
    @PostMapping("/send/{senderId}")
    public ResponseEntity<EmailResponse> sendEmail(
            @PathVariable UUID senderId,
            @Valid @RequestBody SendEmailRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emailService.sendEmail(senderId, request));
    }

    // inbox de uno de los users
    @GetMapping("/inbox/{userId}")
    public ResponseEntity<List<InboxItemResponse>> getInbox(@PathVariable UUID userId) {
        return ResponseEntity.ok(emailService.getInbox(userId));
    }

    // Destacados
    @GetMapping("/starred/{userId}")
    public ResponseEntity<List<InboxItemResponse>> getStarred(@PathVariable UUID userId) {
        return ResponseEntity.ok(emailService.getStarred(userId));
    }

    // Papelera
    @GetMapping("/trash/{userId}")
    public ResponseEntity<List<InboxItemResponse>> getTrash(@PathVariable UUID userId) {
        return ResponseEntity.ok(emailService.getTrash(userId));
    }

    // Borradores
    @GetMapping("/drafts/{userId}")
    public ResponseEntity<List<EmailResponse>> getDrafts(@PathVariable UUID userId) {
        return ResponseEntity.ok(emailService.getDrafts(userId));
    }

    // Correo por id
    @GetMapping("/{emailId}")
    public ResponseEntity<EmailResponse> getEmailById(@PathVariable UUID emailId) {
        return ResponseEntity.ok(emailService.getEmailById(emailId));
    }

    // Marcar como leido
    @PatchMapping("/read/{recipientId}")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID recipientId) {
        emailService.markAsRead(recipientId);
        return ResponseEntity.noContent().build();
    }

    // Toggle start
    @PatchMapping("/star/{recipientId}")
    public ResponseEntity<Void> toggleStar(@PathVariable UUID recipientId) {
        emailService.toggleStar(recipientId);
        return ResponseEntity.noContent().build();
    }

    // Enviar a papelera
    @PatchMapping("/trash/{recipientId}")
    public ResponseEntity<Void> moveToTrash(@PathVariable UUID recipientId) {
        emailService.moveToTrash(recipientId);
        return ResponseEntity.noContent().build();
    }

    // Eliminar Email
    @DeleteMapping("/{emailId}")
    public ResponseEntity<Void> deleteEmail(@PathVariable UUID emailId) {
        emailService.deleteEmail(emailId);
        return ResponseEntity.noContent().build();
    }
}