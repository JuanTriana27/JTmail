package co.jtmail.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "labels")
public class Label {

    /**
     * ID único del label.
     * Se genera automáticamente como UUID (alineado con la DB).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_label", nullable = false, updatable = false)
    private UUID idLabel;

    // Nombre del estado o sitio
    @Column(name = "name", nullable = false)
    private String name;

    // Color
    @Column(name = "color")
    private String color;

    // Is sistem
    /* Indica si el label esta activo en el sistema */
    @Builder.Default
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    // Fecha de creacion
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Llave foranea
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id_user", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
