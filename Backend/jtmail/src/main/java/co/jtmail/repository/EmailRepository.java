package co.jtmail.repository;

import co.jtmail.model.Email;
import co.jtmail.model.User;
import co.jtmail.model.enums.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {

    // Borradores del sender — para la bandeja de borradores
    List<Email> findBySenderIdUserAndStatus(UUID senderId, EmailStatus status);

    // Busca por entidad User
    List<Email> findBySenderAndStatus(User sender, EmailStatus status);
}