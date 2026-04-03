package co.jtmail.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa un usuario del sistema (tipo Gmail).
 * Mapea directamente con la tabla "users" en PostgreSQL.
 * Implementa UserDetails para integrarse con Spring Security y JWT.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * ID único del usuario.
     * Se genera automáticamente como UUID (alineado con la DB).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_user", nullable = false, updatable = false)
    private UUID idUser;

    /* Email del usuario (debe ser único) */
    @Column(name = "email", nullable = false, unique = true, length = 250)
    private String email;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /* Hash de la contraseña (NUNCA guardar texto plano) */
    @Column(name = "password", nullable = false, length = 250)
    private String passwordHash;

    @Column(name = "avatar_url")
    private String avatarUrl;

    /* Indica si el usuario está activo en el sistema */
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /* Contador de correos no leídos */
    @Builder.Default
    @Column(name = "unread_count")
    private Integer unreadCount = 0;

    /* Fecha de creación del registro.
     * Se asigna automáticamente al insertar.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /* Fecha de última actualización.
     * Se actualiza automáticamente en cada modificación.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Hook de JPA que se ejecuta ANTES de insertar en la db
     *
     * Objetivo:
     * - Inicializar timestamps automáticamente
     * - Evitar depender de lógica externa (services/controllers)
     *
     * Se ejecuta cuando:
     * userRepository.save(user) → INSERT (Cuando se guarda un usuario)
     */
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    /**
     * Hook de JPA que se ejecuta ANTES de actualizar el registro.
     *
     * Objetivo:
     * - Mantener actualizado el campo updated_at automáticamente
     *
     * Se ejecuta cuando:
     * userRepository.save(user) → UPDATE (Cuando actualiza un usuario)
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // ─── UserDetails ───────────────────────────────────────────
    // @JsonIgnore evita que Swagger y Jackson intenten serializar
    // estos métodos de Spring Security en las respuestas de la API

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por ahora todos los usuarios tienen el mismo rol
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /* Spring Security necesita getPassword() para verificar credenciales */
    @Override
    @JsonIgnore
    public String getPassword() {
        return passwordHash;
    }

    /* Spring Security usa el email como identificador único del usuario */
    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() { return true; }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() { return true; }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() { return true; }

    /* Delega en isActive para que usuarios desactivados no puedan autenticarse */
    @Override
    @JsonIgnore
    public boolean isEnabled() { return isActive; }
}