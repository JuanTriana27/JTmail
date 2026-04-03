package co.jtmail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabelRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    private String name;

    // Formato hexa
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "El color debe ser un hex válido ej: #FF5733")
    private String color;
}