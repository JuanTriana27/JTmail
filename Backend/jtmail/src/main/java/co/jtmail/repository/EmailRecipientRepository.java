package co.jtmail.repository;

import co.jtmail.model.EmailRecipient;
import co.jtmail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailRecipientRepository extends JpaRepository<EmailRecipient, UUID> {

    // Bandeja de entrada — correos no eliminados ni archivados
    List<EmailRecipient> findByUserAndIsTrashedFalseAndIsArchivedFalse(User user);

    // Destacados
    List<EmailRecipient> findByUserAndIsStarredTrue(User user);

    // Papelera
    List<EmailRecipient> findByUserAndIsTrashedTrue(User user);
}