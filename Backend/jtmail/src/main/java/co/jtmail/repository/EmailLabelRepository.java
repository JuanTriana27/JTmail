package co.jtmail.repository;

import co.jtmail.model.Email;
import co.jtmail.model.EmailLabel;
import co.jtmail.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailLabelRepository extends JpaRepository<EmailLabel, EmailLabel.EmailLabelId> {

    // Todos los labels de un correo específico
    List<EmailLabel> findByEmail(Email email);

    // Todos los correos que tienen un label específico
    List<EmailLabel> findByLabel(Label label);

    // Verificar si ya existe la relación antes de crearla
    boolean existsByEmailAndLabel(Email email, Label label);

    void deleteByEmailAndLabel(Email email, Label label);
}