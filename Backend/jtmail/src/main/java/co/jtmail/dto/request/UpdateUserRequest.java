package co.jtmail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "El nombre completo es requerido")
    @Size(min = 2, max = 150)
    private String fullName;

    // Avatar es opcional en update
    private String avatarUrl;

    // Email y password no se actualizan aquí:
    // - Email es identificador del usuario
    // - Password tiene su propio endpoint /api/users/{id}/password
}