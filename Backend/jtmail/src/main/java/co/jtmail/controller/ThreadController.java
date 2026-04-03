package co.jtmail.controller;

import co.jtmail.dto.response.ThreadResponse;
import co.jtmail.service.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/threads")
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadService threadService;

    // Obtener todo
    @GetMapping
    public ResponseEntity<List<ThreadResponse>> getAllThreads() {
        return ResponseEntity.ok(threadService.getAllThreads());
    }

    // Obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<ThreadResponse> getThreadById(@PathVariable UUID id) {
        return ResponseEntity.ok(threadService.getThreadById(id));
    }

    // Crear Thread
    @PostMapping
    public ResponseEntity<ThreadResponse> createThread() {
        return ResponseEntity.status(HttpStatus.CREATED).body(threadService.createThread());
    }

    // Eliminar thread
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThread(@PathVariable UUID id) {
        threadService.deleteThread(id);
        return ResponseEntity.noContent().build();
    }
}