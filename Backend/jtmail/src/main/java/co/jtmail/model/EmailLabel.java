package co.jtmail.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_labels")
@IdClass(EmailLabel.EmailLabelId.class)
public class EmailLabel {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", referencedColumnName = "id_email", nullable = false)
    private Email email;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id", referencedColumnName = "id_label", nullable = false)
    private Label label;

    // user_id se infiere del label — no necesita mapearse explícitamente
    // la constraint compuesta de la DB se satisface porque label ya tiene user_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id_user", nullable = false)
    private User user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailLabelId implements Serializable {
        private UUID email;
        private UUID label;
    }
}