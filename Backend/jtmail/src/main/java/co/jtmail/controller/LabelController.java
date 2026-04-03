package co.jtmail.controller;

import co.jtmail.dto.request.CreateLabelRequest;
import co.jtmail.dto.response.LabelResponse;
import co.jtmail.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    // Labels anidados bajo el usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LabelResponse>> getLabelsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(labelService.getLabelsByUser(userId));
    }

    // Obtener por id label
    @GetMapping("/{id}")
    public ResponseEntity<LabelResponse> getLabelById(@PathVariable UUID id) {
        return ResponseEntity.ok(labelService.getLabelById(id));
    }

    // Crer label con id
    @PostMapping("/user/{userId}")
    public ResponseEntity<LabelResponse> createLabel(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateLabelRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labelService.createLabel(userId, request));
    }

    // actualizar label con id
    @PutMapping("/{id}")
    public ResponseEntity<LabelResponse> updateLabel(
            @PathVariable UUID id,
            @Valid @RequestBody CreateLabelRequest request
    ) {
        return ResponseEntity.ok(labelService.updateLabel(id, request));
    }

    // ELMINAR label
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable UUID id) {
        labelService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }
}