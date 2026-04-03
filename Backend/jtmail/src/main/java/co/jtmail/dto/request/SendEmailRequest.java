package co.jtmail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequest {

    @NotEmpty(message = "Debe haber al menos un destinatario")
    private List<UUID> to;

    // CC y BCC son opcionales
    private List<UUID> cc;
    private List<UUID> bcc;

    @NotBlank(message = "El asunto es requerido")
    private String subject;

    @NotBlank(message = "El cuerpo es requerido")
    private String body;

    // Si es null se crea un thread nuevo, si tiene valor es una respuesta
    private UUID threadId;
}