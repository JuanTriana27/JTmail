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
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_attachment", nullable = false, updatable = false)
    private UUID idAttachment;

    /* Correo al que pertenece este adjunto */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", referencedColumnName = "id_email", nullable = false)
    private Email email;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /* Tamaño en bytes */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    /* URL donde vive el archivo — Cloudinary, S3, etc. */
    @Column(name = "storage_url", nullable = false, length = 500)
    private String storageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}