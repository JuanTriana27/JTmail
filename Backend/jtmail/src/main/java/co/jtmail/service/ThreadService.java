package co.jtmail.service;

import co.jtmail.dto.response.ThreadResponse;

import java.util.List;
import java.util.UUID;

public interface ThreadService {

    // Obtener todos los threads
    List<ThreadResponse> getAllThreads();

    // Obtener por id
    ThreadResponse getThreadById(UUID id);

    // Crear
    ThreadResponse createThread();   // sin request — no necesita datos externos

    // Eliminar
    void deleteThread(UUID id);
}