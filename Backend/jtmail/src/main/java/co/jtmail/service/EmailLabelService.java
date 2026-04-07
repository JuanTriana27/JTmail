package co.jtmail.service;

import co.jtmail.dto.response.EmailLabelResponse;

import java.util.List;
import java.util.UUID;

public interface EmailLabelService {

    // Obtener todos los email label
    List<EmailLabelResponse> getLabelsByEmail(UUID emailId);

    // Añadir label a email
    EmailLabelResponse addLabelToEmail(UUID emailId, UUID labelId);

    // Eliminar label de email
    void removeLabelFromEmail(UUID emailId, UUID labelId);
}