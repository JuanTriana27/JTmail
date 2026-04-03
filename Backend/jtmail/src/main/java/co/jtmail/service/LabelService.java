package co.jtmail.service;

import co.jtmail.dto.request.CreateLabelRequest;
import co.jtmail.dto.response.LabelResponse;

import java.util.List;
import java.util.UUID;

public interface LabelService {

    // Obtener todos los labels
    List<LabelResponse> getLabelsByUser(UUID userId);

    // Obtener por id
    LabelResponse getLabelById(UUID id);

    // Crear label
    LabelResponse createLabel(UUID userId, CreateLabelRequest request);

    // Actualizar label
    LabelResponse updateLabel(UUID id, CreateLabelRequest request);

    // Eliminar label
    void deleteLabel(UUID id);
}