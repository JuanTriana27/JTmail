package co.jtmail.repository;

import co.jtmail.model.Label;
import co.jtmail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {

    // Todos los labels de un usuario (sistema + personalizados)
    List<Label> findByUser(User user);

    // Evitamos labels duplicados por nombre para el mismo usuario
    boolean existsByUserAndName(User user, String name);
}
