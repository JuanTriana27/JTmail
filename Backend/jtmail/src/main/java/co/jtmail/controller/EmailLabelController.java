package co.jtmail.controller;

import co.jtmail.dto.response.EmailLabelResponse;
import co.jtmail.service.EmailLabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/email-labels")
@RequiredArgsConstructor
public class EmailLabelController {

    private final EmailLabelService emailLabelService;

    // Todos los labels de un correo
    @GetMapping("/{emailId}")
    public ResponseEntity<List<EmailLabelResponse>> getLabelsByEmail(@PathVariable UUID emailId) {
        return ResponseEntity.ok(emailLabelService.getLabelsByEmail(emailId));
    }

    // Asignar label a correo
    @PostMapping("/{emailId}/labels/{labelId}")
    public ResponseEntity<EmailLabelResponse> addLabel(
            @PathVariable UUID emailId,
            @PathVariable UUID labelId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emailLabelService.addLabelToEmail(emailId, labelId));
    }

    // Quitar label de correo
    @DeleteMapping("/{emailId}/labels/{labelId}")
    public ResponseEntity<Void> removeLabel(
            @PathVariable UUID emailId,
            @PathVariable UUID labelId
    ) {
        emailLabelService.removeLabelFromEmail(emailId, labelId);
        return ResponseEntity.noContent().build();
    }
}