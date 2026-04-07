package co.jtmail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAttachmentRequest {

    @NotNull(message = "El ID del correo es requerido")
    private UUID emailId;

    @NotBlank(message = "El nombre del archivo es requerido")
    private String fileName;

    @NotNull(message = "El tamaño es requerido")
    @Positive(message = "El tamaño debe ser mayor a 0")
    private Long fileSize;

    @NotBlank(message = "El tipo MIME es requerido")
    private String mimeType;

    // Por ahora URL directa — cuando llegue Cloudinary esto se reemplaza
    // por la URL que devuelve el servicio de storage
    @NotBlank(message = "La URL de almacenamiento es requerida")
    private String storageUrl;
}