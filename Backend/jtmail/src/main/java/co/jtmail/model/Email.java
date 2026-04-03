package co.jtmail.model;

import jakarta.persistence.*;
import co.jtmail.model.enums.BodyType;
import co.jtmail.model.enums.EmailStatus;
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
@Table(name = "emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_email", nullable = false, updatable = false)
    private UUID idEmail;

    /* Hilo al que pertenece este correo */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", referencedColumnName = "id_thread", nullable = false)
    private MailThread thread;

    /* Usuario que envía el correo */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id_user", nullable = false)
    private User sender;

    @Column(name = "subject", nullable = false, length = 500)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    /* HTML o PLAIN — por defecto HTML */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "body_type", nullable = false)
    private BodyType bodyType = BodyType.HTML;

    /* DRAFT mientras no se envía, SENT cuando se despacha */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmailStatus status = EmailStatus.DRAFT;

    /* Solo se asigna cuando el correo se envía efectivamente */
    @Column(name = "sent_at")
    private Instant sentAt;

    /* Soft delete — no se borra físicamente */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}