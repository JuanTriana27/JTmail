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
@Table(name = "threads")
public class MailThread {
    /**
     * ID único del thread.
     * Se genera automáticamente como UUID (alineado con la DB).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_thread", nullable = false, updatable = false)
    private UUID idThread;

    /* Fecha de creacion del thread*/
    @Column (name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /* Fecha de ultimo email, es updatable ya que cada que llega un correo se actualiza */
    @Column (name = "last_email_at", nullable = false)
    private Instant lastEmailAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        lastEmailAt = Instant.now();
    }
}
