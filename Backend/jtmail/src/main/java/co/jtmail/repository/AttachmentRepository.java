package co.jtmail.repository;

import co.jtmail.model.Attachment;
import co.jtmail.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    // Todos los adjuntos de un correo
    List<Attachment> findByEmail(Email email);
}