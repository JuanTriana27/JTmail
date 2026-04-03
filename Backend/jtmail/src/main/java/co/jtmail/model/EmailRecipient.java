package co.jtmail.model;

import co.jtmail.model.enums.RecipientType;
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
@Table(name = "email_recipients")
public class EmailRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_recipient", nullable = false, updatable = false)
    private UUID idRecipient;

    /* Correo al que pertenece este destinatario */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", referencedColumnName = "id_email", nullable = false)
    private Email email;

    /* Usuario destinatario */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id_user", nullable = false)
    private User user;

    /* TO, CC o BCC */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RecipientType type;

    /* Estado individual por destinatario — cada uno tiene el suyo */
    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Builder.Default
    @Column(name = "is_starred", nullable = false)
    private Boolean isStarred = false;

    @Builder.Default
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @Builder.Default
    @Column(name = "is_trashed", nullable = false)
    private Boolean isTrashed = false;

    /* Se asigna cuando el destinatario abre el correo */
    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}